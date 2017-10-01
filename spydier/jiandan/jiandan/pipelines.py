# -*- coding: utf-8 -*-

# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: http://doc.scrapy.org/en/latest/topics/item-pipeline.html
import scrapy
from scrapy import log
import json
import os
from scrapy.contrib.pipeline.images import ImagesPipeline
from scrapy.pipelines.files import FileException, FilesPipeline
from scrapy.utils.python import to_bytes
try:
    from cStringIO import StringIO as BytesIO
except ImportError:
    from io import BytesIO
from PIL import Image
from scrapy.utils.misc import md5sum
from scrapy.exceptions import DropItem
import MySQLdb
import string
class BadEggPipeline(object):
    def __init__(self,dbname):
        self.dbname = dbname
    @classmethod
    def from_crawler(cls,crawler):
        return cls(dbname="nest")
    def open_spider(self,spider):
        self.conn = MySQLdb.connect(host="localhost",port=3306,user='root',passwd='7991205aa',db=self.dbname)
        self.cur = self.conn.cursor()
        self.cur.execute("create table IF NOT EXISTS eggs_nest(itemid varchar(64),checksum varchar(32),support int)")
        self.conn.commit()
    def close_spider(self,spider):
        self.cur.close()
        self.conn.close();
    def process_item(self,item,spider):
        self.cur.execute("select count(itemid) from eggs_nest where itemid = '"+item['itemId']+"'")
        count =  int(self.cur.fetchone()[0])
        if count >=1 :
            log.msg("*********************", level=log.WARNING)
            raise DropItem("duplicated egg found:%s" % item)
        return item
    
    
class JiandanPipeline(object):
    def process_item(self, item, spider):
        if len(item['href'])>0:
            arr = []
            for href in item['href']:
                arr.append("http:"+href)
            item['href'] = arr;
        if len(item['img'])>0:
            arr = []
            for img in item['img']:
                arr.append("http:"+img)
            item['img'] = arr
        return item

class JsonWriterPipeline(object):
    def __init__(self):
        self.file = open('items.jl','wb')
    
    def process_item(self, item , spider):
        line = json.dumps(dict(item))+"\n"
        self.file.write(line)
        return item 

class NestPipeline(object):
    def __init__(self,dbname):
        self.dbname = dbname
    @classmethod
    def from_crawler(cls,crawler):
        return cls(dbname="nest")
    def open_spider(self,spider):
        self.conn = MySQLdb.connect(host="localhost",port=3306,user='root',passwd='7991205aa',db=self.dbname)
        self.cur = self.conn.cursor()
        self.cur.execute("create table IF NOT EXISTS eggs_nest(itemid varchar(64),checksum varchar(32),support int)")
        self.cur.execute("create table IF NOT EXISTS eggs_href(itemid varchar(64), img varchar(1000), href varchar(1000))")
        self.conn.commit()
    def close_spider(self,spider):
        self.cur.close()
        self.conn.close();
    def process_item(self,item,spider):
        hrefs = item['href']
        imgs = item['img']
        self.cur.execute("insert into eggs_nest VALUES('"+item['itemId']+"','"+item['checksum'] + "' ," + str(item['support'])+")")
        for i in range(0,len(hrefs)):
            self.cur.execute("insert into eggs_href VALUES('"+item['itemId']+"','"+imgs[i]+"','"+hrefs[i]+"')")
        self.conn.commit()
        return item
    
    
class ImageDownloaderPipeline(ImagesPipeline):
    def get_media_requests(self,item,info):
        for img in item['img']:
            yield scrapy.Request(img)
        for href in item['href']:
            yield scrapy.Request(href)
    def item_completed(self,results,item,info):
        # [(True, {'url': 'http://wx3.sinaimg.cn/mw600/7c4f157bly1fjttyr2k98j207x0aiaat.jpg', 'path': 'full/f008182965ea5d2088538e2abd72156245390ebd.jpg', 'checksum': '271fc88ab5450750bc450ce88e79a062'}), (True, {'url': 'http://wx3.sinaimg.cn/large/7c4f157bly1fjttyr2k98j207x0aiaat.jpg', 'path': 'full/155e7f24481a85f8365f0841ad966a7b785bb5d8.jpg', 'checksum': '271fc88ab5450750bc450ce88e79a062'})]
	item['checksum'] = None
        for result in results:
            if True == result[0] and item['href'].count(result[1]['url']): 
                index = item['href'].index(result[1]['url'])  
		if result[1]['url'][-3:] == 'gif':
		    result[1]['path'] = result[1]['path'][:-3]+'gif' 
                item['href'][index] = result[1]['path']
                item['checksum'] = result[1]['checksum']
            if True == result[0] and item['img'].count(result[1]['url']): 
                index = item['img'].index(result[1]['url'])
		if result[1]['url'][-3:] == 'gif':
		    result[1]['path'] = result[1]['path'][:-3]+'gif' 
                item['img'][index] = result[1]['path']
        if item['checksum'] is None:
            raise DropItem("duplicated egg found:%s" % item)
        else:
            return item

    def check_gif(self, image):
        if image.format == 'GIF':
            return True
        # The library reads GIF87a and GIF89a versions of the GIF file format.
        return image.info.get('version') in ['GIF89a', 'GIF87a']

    def persist_gif(self, key, data, info):
        root, ext = os.path.splitext(key)
        key = key[0:len(key)-3] + 'gif'
        absolute_path = self.store._get_filesystem_path(key)
        self.store._mkdir(os.path.dirname(absolute_path), info)
        f = open(absolute_path, 'wb')   # use 'b' to write binary data.
        f.write(data)

    def image_downloaded(self, response, request, info):
        checksum = None
        for key, image, buf in self.get_images(response, request, info):
	    isgif = self.check_gif(image)
            if checksum is None and True != isgif:
                buf.seek(0)
                checksum = md5sum(buf)
            if isgif: 
                # Save gif from response directly.
                self.persist_gif(key, response.body, info)
		checksum = 'gif'
            else:
                width, height = image.size
                self.store.persist_file(key, buf, info,meta={'width': width, 'height': height},headers={'Content-Type': 'image/jpeg'})
        return checksum
     
    def convert_image(self, image, size=None):
        buf = BytesIO()
        if image.format == 'GIF':
            	return image,buf
        elif image.format == 'PNG' and image.mode == 'RGBA':
            background = Image.new('RGBA', image.size, (255, 255, 255))
            background.paste(image, image)
            image = background.convert('RGB')
        elif image.mode == 'P':
            image = image.convert("RGBA")
            background = Image.new('RGBA', image.size, (255, 255, 255))
            background.paste(image, image)
            image = background.convert('RGB')
        elif image.mode != 'RGB':
            image = image.convert('RGB')

        image.save(buf, 'JPEG')
        return image, buf
