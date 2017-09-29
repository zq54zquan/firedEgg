local dbhelp=require 'db.db_help'
local code,db = dbhelp:con_db()
local cjson = require "cjson" 
local function support(itemid,uptype)
   if code ~= dbhelp.DB_CONN_FAILED then 
        local st,res = dbhelp:db_update_with_itemid(db,itemid,uptype) 
        if st == dbhelp.DB_QUERY_FAILED then 
            ngx.say(cjson.encode({code = 400}))
        else                 
            ngx.say(cjson.encode({code = 200,support = res}))
        end                
   end           
end
local args = ngx.req.get_uri_args()
local itemid= args["id"]
local support_type = args["support"]
if nil == itemid then 
    ngx.say(cjson.encode({code=400,message='itemid== nil'}))
else
    support(itemid,support_type)
end    
