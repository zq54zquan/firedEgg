local dbhelp = require "db.db_help"
local function strSplit(delimeter, str)  
    local find, sub, insert = string.find, string.sub, table.insert  
    local res = {}  
    local start, start_pos, end_pos = 1, 1, 1  
    while true do  
        start_pos, end_pos = find(str, delimeter, start, true)  
        if not start_pos then  
            break  
        end  
        insert(res, sub(str, start, start_pos - 1))  
        start = end_pos + 1    
    end  
    insert(res, sub(str,start))  
    return res  
end  

local function get_data(page,page_size) 
	local dbhelp = require "db.db_help" 
	local c,db = dbhelp:con_db()
	local st,res = dbhelp:db_query_with_page(db,page,page_size,dbhelp.DB_ORDER_TYPE_SUPPORT)
	if st == dbhelp.DB_QUERY_SUCCESS then 
		local data = res
		for v =1,#data do
		    local hd = res[v]['hd']
		    hd = strSplit(',',hd)
		    local img = res[v]['thumb']
		    img = strSplit(',',img)
		    res[v]['hd']=hd
	            res[v]['thumb']=img
		end
		local result = cjson.encode({code=200,data=res});
		ngx.say(result)
	else 
		ngx.say(cjson.encode({code=200,data=nil}))
	end
end
local args = ngx.req.get_uri_args()
local page = args["page"] or 0
local pagesize = args["size"] or 10
get_data(page,pagesize)
