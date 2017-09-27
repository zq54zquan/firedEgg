local _M = {}
_M.DB_CONN_FAILED = 0 ---- 连接失败
_M.DB_CONN_SUCCESS = 1 ---- 连接成功
_M.DB_ORDER_TYPE_NATURAL = 0 ---- 数据库自然排序
_M.DB_ORDER_TYPE_SUPPORT = 1 ---- 赞数排序
_M.DB_QUERY_FAILED = 0  ---- 数据库请求失败
_M.DB_QUERY_SUCCESS = 1  ---- 数据库请求失败
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
        order_query = "order by support "
    end
    local res,err,errcode,sqlstate = db:query("select * from eggs_nest "..order_query..'limit'..page*page_size..","..page_size)       
    if not res then 
        return self.DB_QUERY_FAILED,nil
    end
    local cjson = require "cjson"
    ngx.say("#"..cjson.encode(res));
    while err == 'again' do 
        res,err,errcode,sqlstate = db.read_result()
        if not res then 
            return self.DB_QUERY_FAILED,nil
        end
        ngx.say("#"..cjson.encode(res));
    end
end
return _M
