-- 定义 Lua 脚本
local function getRandomElementsFromList(redis_key, num_elements)
    -- 获取列表的所有元素
    local elements = redis.call('LRANGE', redis_key, 0, -1)

    -- 初始化结果列表
    local result = {}

    -- 检查元素数量是否足够
    if #elements < num_elements then
        error("Not enough elements in the list: " .. redis_key)
    end

    -- 随机选择元素
    for i = 1, num_elements do
        -- 生成一个随机索引
        local random_index = math.random(1, #elements)

        -- 将选中的元素加入结果列表
        table.insert(result, elements[random_index])

        -- 从列表中移除已选中的元素，防止重复选择
        table.remove(elements, random_index)
    end

    return result
end

-- 主函数
local function main(prefix, counts)
    -- 初始化最终结果列表
    local final_result = {}

    -- 遍历每个子键
    for i = 1, #counts do
        local redis_key = prefix .. tostring(i - 1)
        local num_elements = tonumber(counts[i])

        -- 从子键中随机选择指定数量的元素
        local selected_elements = getRandomElementsFromList(redis_key, num_elements)

        -- 将选中的元素加入最终结果列表
        for _, element in ipairs(selected_elements) do
            table.insert(final_result, element)
        end
    end

    return final_result
end

-- 获取输入参数
local prefix = KEYS[1]
local counts = ARGV

-- 执行主函数
local result = main(prefix, counts)

-- 返回结果
return result