<template>
  <div class="map-container">
    <div id="map" style="height: 500px;"></div>
  </div>
</template>

<script>
import { onMounted, ref } from 'vue'
import 'leaflet/dist/leaflet.css'
import L from 'leaflet'
import axios from 'axios'

export default {
  setup() {
    const scooters = ref([])
    
    onMounted(() => {
      const map = L.map('map').setView([51.505, -0.09], 13)
      
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
      }).addTo(map)
      
      // 获取并显示电动滑板车位置
      fetchScooters().then(data => {
        data.forEach(scooter => {
          L.marker([scooter.latitude, scooter.longitude])
            .addTo(map)
            .bindPopup(`Scooter #${scooter.id}`)
        })
      })
    })
    
    const fetchScooters = async () => {
      const response = await axios.get('/api/scooters')
      return response.data
    }
    
    return {
      scooters
    }
  }
}
</script> 