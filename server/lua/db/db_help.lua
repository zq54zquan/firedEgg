local _M = {}
_M.DB_CONN_FAILED = 0 ---- 连接失败
_M.DB_CONN_SUCCESS = 1 ---- 连接成功
_M.DB_ORDER_TYPE_NATURAL = 0 ---- 数据库自然排序
_M.DB_ORDER_TYPE_SUPPORT = 1 ---- 赞数排序
_M.DB_QUERY_FAILED = 0  ---- 数据库请求失败
_M.DB_QUERY_SUCCESS = 1  ---- 数据库请求失败
_M.DB_UPDATE_ADD = 1  ---- 数据库请求失败
_M.DB_UPDATE_SUB = 0  ---- 数据库请求失败 

--------------------------------------------------------------------------------
-- 连接数据库，返回数据库成功标记和数据库对象db
--------------------------------------------------------------------------------
function _M.con_db(self)
    local mysql = require "resty.mysql"
    local db,err = mysql.new()
    if not db then 
        return self.DB_CONN_FAILED,nil;
    end
         
    db:set_timeout(1000) --- 1 second
    local ok,err,errcode,sqlstate = db:connect{
        host = '127.0.0.1',
        port = '3306',
        database = 'nest',
        user = 'root',
        password = '7991205aa',
        charset = 'utf8',
        max_packet_size = 1024*1024
    }

    if not ok then 
        return self.DB_CONN_FAILED,nil
    end        
    return DB_CONN_SUCCESS,db 
end

--------------------------------------------------------------------------------
-- 查找数据
-- 参数 db对象,页数,大小,排序类型
--------------------------------------------------------------------------------
function _M.db_query_with_page(self,db,page,page_size,order_type)
    local order_query = ""
    if self.DB_ORDER_TYPE_SUPPORT == order_type then
        order_query = "order by eggs_nest.support desc "
    end
    local res,err,errcode,sqlstate = db:query("select eggs_nest.itemid,eggs_nest.support,GROUP_CONCAT(eggs_href.href),GROUP_CONCAT(eggs_href.img) from eggs_nest left join eggs_href on eggs_nest.itemid = eggs_href.itemid group by eggs_nest.itemid "..order_query..'limit '..page*page_size..","..page_size)       
    if not res then 
		ngx.log(ngx.ERR,err)
        return self.DB_QUERY_FAILED,nil
    end
	return self.DB_QUERY_SUCCESS,res
end

function _M.db_update_with_itemid(self,db,itemid,support_type) 
    local res,err,errcode,sqlstate = db:query("select support from eggs_nest where itemid= '"..itemid.."'")       
    if not res then 
        return self.DB_QUERY_FAILED,nil            
    end
	local originsup = tonumber(res[1]["support"])
	if tonumber(support_type) == self.DB_UPDATE_ADD then 
    	res,err,errcode,sqlstate = db:query("update eggs_nest set  support = "..tostring(originsup+1).." where itemid = "..itemid)       
		if res then 
			return self.DB_QUERY_SUCCESS,originsup+1
	 	else 
			return self.DB_QUERY_FAILED,nil
		end
		
	else 
    	res,err,errcode,sqlstate = db:query("update eggs_nest set  support = "..tostring(originsup-1).." where itemid = "..itemid)       
		if res then 
			return self.DB_QUERY_SUCCESS,originsup-1
	 	else 
			return self.DB_QUERY_FAILED,nil
		end
	end
end        
return _M
