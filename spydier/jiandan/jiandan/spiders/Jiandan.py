import scrapy
from jiandan.items import JiandanItem
from scrapy.contrib.spiders import CrawlSpider,Rule
from scrapy.contrib.linkextractors import LinkExtractor
class JiandanSpider(CrawlSpider):
    name = "jiandan"
    start_urls = ["http://jandan.net/ooxx"]
    allow_domains = ["jandan.net"];
    rules=[Rule(LinkExtractor(allow=['/page-\d+#comments']),callback='parsexx',follow=True)]
    def parsexx(self, response):
        for sel in response.xpath('//ol[@class="commentlist"]/li/div/div[@class="row"]'):
            i = JiandanItem()
            i['href'] = sel.xpath('div[@class="text"]//a[@class="view_img_link"]/@href').extract()
            i['support']=int(sel.xpath('div[@class="jandan-vote"]/span[@class="tucao-like-container"]/span[1]/text()').extract()[0])
            i['img'] = sel.xpath('div[@class="text"]//img/@src').extract()
            i['itemId'] =  sel.xpath('div[@class="jandan-vote"]/span[@class="tucao-like-container"]/a[1]/@data-id').extract()[0]
            i['img_width']=scrapy.Field()
            i['img_height']=scrapy.Field()
            i['href_width']=scrapy.Field()
            i['href_height']=scrapy.Field()
            yield i;
