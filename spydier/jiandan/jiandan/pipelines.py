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
    
class ImageDownloaderPipeline(ImagesPipeline):
    def get_media_requests(self,item,info):
        for img in item['img']:
            yield scrapy.Request(img)
        for href in item['href']:
            yield scrapy.Request(href)
    def item_completed(self,results,item,info):
        print results
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
            log.msg(key+"===============", level=log.WARNING)
            if checksum is None:
                buf.seek(0)
                checksum = md5sum(buf)
            if  self.check_gif(image):
                # Save gif from response directly.
                self.persist_gif(key, response.body, info)
            else:
                width, height = image.size
                self.store.persist_file(key, buf, info,meta={'width': width, 'height': height},headers={'Content-Type': 'image/jpeg'})
        return checksum
     
    def convert_image(self, image, size=None):
        buf = BytesIO()
        if image.format == 'GIF':
            buf.write(image.data())
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

        if size:
            image = image.copy()
            image.thumbnail(size, Image.ANTIALIAS)

        image.save(buf, 'JPEG')
        return image, buf
