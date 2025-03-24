<template>
  <div class="map-container">
    <el-card class="map-card">
      <template #header>
        <div class="card-header">
          <span>滑板车位置地图</span>
          <el-button type="primary" @click="refreshScooters">刷新位置</el-button>
        </div>
      </template>
      <div id="map" class="map"></div>
      <div class="scooter-list">
        <el-table :data="scooters" height="300">
          <el-table-column prop="id" label="编号" width="80" />
          <el-table-column prop="status" label="状态">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="batteryLevel" label="电量">
            <template #default="{ row }">
              <el-progress :percentage="row.batteryLevel" :status="getBatteryStatus(row.batteryLevel)" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button 
                type="primary" 
                size="small" 
                :disabled="!isScooterAvailable(row)"
                @click="bookScooter(row)">
                租用
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const scooters = ref([])
let map = null
let markers = []

// 初始化地图
const initMap = () => {
  map = new AMap.Map('map', {
    zoom: 13,
    center: [116.397428, 39.90923] // 默认中心点，可以根据实际情况调整
  })
}

// 获取所有滑板车
const fetchScooters = async () => {
  try {
    const response = await api.get('/scooters')
    scooters.value = response
    updateMarkers()
  } catch (error) {
    ElMessage.error('获取滑板车信息失败')
  }
}

// 更新地图标记
const updateMarkers = () => {
  // 清除现有标记
  markers.forEach(marker => marker.remove())
  markers = []

  // 添加新标记
  scooters.value.forEach(scooter => {
    const marker = new AMap.Marker({
      position: [scooter.longitude, scooter.latitude],
      icon: getMarkerIcon(scooter.status),
      title: `滑板车 #${scooter.id}`
    })

    marker.on('click', () => {
      map.setCenter([scooter.longitude, scooter.latitude])
      showScooterInfo(scooter)
    })

    marker.setMap(map)
    markers.push(marker)
  })
}

// 获取标记图标
const getMarkerIcon = (status) => {
  const colors = {
    AVAILABLE: '#67C23A',
    IN_USE: '#E6A23C',
    MAINTENANCE: '#F56C6C'
  }
  return new AMap.Icon({
    size: new AMap.Size(25, 34),
    imageSize: new AMap.Size(25, 34),
    imageOffset: new AMap.Pixel(0, 0),
    image: `https://webapi.amap.com/theme/v1.3/markers/n/mark_${colors[status]}.png`
  })
}

// 显示滑板车信息
const showScooterInfo = (scooter) => {
  const content = `
    <div class="info-window">
      <h4>滑板车 #${scooter.id}</h4>
      <p>状态：${getStatusText(scooter.status)}</p>
      <p>电量：${scooter.batteryLevel}%</p>
    </div>
  `
  const infoWindow = new AMap.InfoWindow({
    content: content,
    offset: new AMap.Pixel(0, -30)
  })
  infoWindow.open(map, [scooter.longitude, scooter.latitude])
}

// 刷新滑板车位置
const refreshScooters = () => {
  fetchScooters()
}

// 租用滑板车
const bookScooter = async (scooter) => {
  try {
    await api.post('/bookings', {
      scooterId: scooter.id,
      userId: localStorage.getItem('userId')
    })
    ElMessage.success('预订成功')
    fetchScooters()
  } catch (error) {
    ElMessage.error('预订失败')
  }
}

// 状态相关工具函数
const getStatusText = (status) => {
  const statusMap = {
    AVAILABLE: '可用',
    IN_USE: '使用中',
    MAINTENANCE: '维护中'
  }
  return statusMap[status] || status
}

const getStatusType = (status) => {
  const typeMap = {
    AVAILABLE: 'success',
    IN_USE: 'warning',
    MAINTENANCE: 'danger'
  }
  return typeMap[status] || 'info'
}

const getBatteryStatus = (level) => {
  if (level <= 20) return 'exception'
  if (level <= 50) return 'warning'
  return 'success'
}

const isScooterAvailable = (scooter) => {
  return scooter.status === 'AVAILABLE' && scooter.batteryLevel > 20
}

// 生命周期钩子
onMounted(() => {
  initMap()
  fetchScooters()
})

onUnmounted(() => {
  if (map) {
    map.destroy()
  }
})
</script>

<style scoped>
.map-container {
  padding: 20px;
}

.map-card {
  margin: 0 auto;
  max-width: 1200px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.map {
  width: 100%;
  height: 500px;
  margin-bottom: 20px;
}

.scooter-list {
  margin-top: 20px;
}

.info-window {
  padding: 10px;
}

.info-window h4 {
  margin: 0 0 10px 0;
}

.info-window p {
  margin: 5px 0;
}
</style> 