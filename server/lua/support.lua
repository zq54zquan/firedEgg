local dbhelp=require 'db.db_help'
local code,db = dbhelp:con_db()
local cjson = require "cjson" 
local function support(checksum,uptype)
   if code ~= dbhelp.DB_CONN_FAILED then 
        local st,res = dbhelp:db_update_with_checksum(db,checksum,uptype) 
        if dbhelp.DB_QUERY_FAILED then 
            ngx.say(cjson.encode({code = 400}))
        else                 
            ngx.say(cjson.encode({code = 200}))
        end                
   end           
end
local args = ngx.req.get_uri_args()
local checksum = args["ck"]
local support_type = args["support"]
if nil == checksum then 
    ngx.say(cjson.encode({code=400,message='checksum == nil'}))
else
    support(checksum,support_type)
end    
