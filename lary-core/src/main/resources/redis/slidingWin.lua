local key = KEYS[1]
local count = tonumber(ARGV[1])
-- milliseconds
local size = tonumber(ARGV[2])
local v = ARGV[3]

redis.replicate_commands()

local date = redis.call("time")
local now = tonumber(date[1])*1000 + tonumber(date[2])
local futureEnd = now + 5000000
local start = now - size * 1000
local expire = tonumber(math.ceil(size / 1000)) + 1
local num = tonumber(redis.call('zcount',key,start,futureEnd))

if num + 1 > count then
    return false
else
    redis.call('zadd',key,now,v)
    redis.call('zremrangebyscore',key,0,start -1000000)
    redis.call('expire',key,expire)
end