#!/bin/bash
################################################################################
####    user this script to start spider and use little python script to 
####    transform the data to database (mysql)
################################################################################
cd spydier/jiandan
scrapy crawl jiandan & python savedata.py
