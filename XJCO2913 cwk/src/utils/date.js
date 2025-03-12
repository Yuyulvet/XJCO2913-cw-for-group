/**
 * 格式化日期时间
 * @param {string|Date} date - 要格式化的日期
 * @returns {string} - 格式化后的日期字符串
 */
export const formatTime = (date) => {
  if (!date) return ''
  
  const d = new Date(date)
  if (isNaN(d.getTime())) return ''

  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

/**
 * 获取相对时间描述
 * @param {string|Date} date - 要计算的日期
 * @returns {string} - 相对时间描述
 */
export const getRelativeTime = (date) => {
  if (!date) return ''
  
  const d = new Date(date)
  if (isNaN(d.getTime())) return ''

  const now = new Date()
  const diff = now.getTime() - d.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 30) return `${days}天前`
  
  return formatTime(date)
}

/**
 * 格式化持续时间（分钟转换为可读格式）
 * @param {number} minutes - 持续时间（分钟）
 * @returns {string} - 格式化后的持续时间
 */
export const formatDuration = (minutes) => {
  if (!minutes || minutes < 0) return '0分钟'
  
  const hours = Math.floor(minutes / 60)
  const remainingMinutes = minutes % 60
  
  if (hours === 0) return `${remainingMinutes}分钟`
  if (remainingMinutes === 0) return `${hours}小时`
  return `${hours}小时${remainingMinutes}分钟`
} 