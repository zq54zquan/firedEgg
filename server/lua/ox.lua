local dbhelp = require "db.db_help"
local code,db = dbhelp:con_db()
local function get_data(page,page_size) 
    if code ~=  dbhelp.DB_CONN_FAILED then
        local st,res = dbhelp:db_query_with_page(db,page,page_size,dbhelp.DB_ORDER_TYPE_SUPPORT)
		if st == dbhelp.DB_CONN_SUCCESS then 
			local cjson = require "cjson"
			ngx.say(cjson.encode({code=200,data=res}));
		else 
			ngx.say(cjson.encode({code=200,data=nil}))
		end
    else
		ngx.say(cjson.encode({code=200,data=nil}))
    end
end
local args = ngx.req.get_uri_args()
local page = args["page"] or 0
local pagesize = args["size"] or 10
get_data(page,pagesize)
