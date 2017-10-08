# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html

import scrapy


class JiandanItem(scrapy.Item):
	href = scrapy.Field()
	support = scrapy.Field()
	img = scrapy.Field()	
	checksum = scrapy.Field()
	itemId = scrapy.Field()
	img_type = scrapy.Field()
	img_width = scrapy.Field()
	img_height = scrapy.Field()
	href_type = scrapy.Field()
	href_width = scrapy.Field()
	href_height = scrapy.Field()
	ctime = scrapy.Field()
