<template>
  <div class="home-container">
    <div id="map-container" class="map-container">
      <el-card v-if="!mapLoaded" class="loading-card">
        <el-skeleton :rows="3" animated />
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'

const map = ref(null)
const currentPosition = ref(null)
const mapLoaded = ref(false)

const initMap = (position) => {
  try {
    const { longitude, latitude } = position
    map.value = new AMap.Map('map-container', {
      zoom: 15,
      center: [longitude, latitude],
      resizeEnable: true
    })
    
    // 添加定位标记
    new AMap.Marker({
      position: [longitude, latitude],
      map: map.value,
      title: '当前位置'
    })

    mapLoaded.value = true
  } catch (error) {
    console.error('地图初始化失败:', error)
    ElMessage.error('地图加载失败，请刷新页面重试')
  }
}

const getCurrentPosition = () => {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error('您的浏览器不支持地理位置功能'))
      return
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        console.log('获取到位置:', position)
        const coords = {
          longitude: position.coords.longitude,
          latitude: position.coords.latitude
        }
        currentPosition.value = coords
        resolve(coords)
      },
      (error) => {
        console.error('获取位置失败:', error)
        reject(error)
      },
      {
        enableHighAccuracy: true,
        timeout: 5000,
        maximumAge: 0
      }
    )
  })
}

onMounted(async () => {
  try {
    console.log('正在获取位置...')
    const position = await getCurrentPosition()
    console.log('初始化地图...')
    initMap(position)
  } catch (error) {
    console.error('位置获取失败:', error)
    ElMessage.error('获取位置信息失败：' + error.message)
    // 默认定位到北京
    initMap({ longitude: 116.397428, latitude: 39.90923 })
  }
})
</script>

<style scoped>
.home-container {
  width: 100%;
  height: calc(100vh - 60px);
  position: relative;
}

.map-container {
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
}

.loading-card {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 300px;
}
</style> 