local dbhelp = require "db.db_help"
local code,db = dbhelp:con_db()
local function get_data(page) 
    if code ~=  dbhelp.DB_CONN_FAILED then
        ngx.say('dd');
        dbhelp:db_query_with_page(db,page,10,dbhelp.DB_ORDER_TYPE_SUPPORT)
    else
        ngx.say("connect failed")  
    end
end
get_data(0)
