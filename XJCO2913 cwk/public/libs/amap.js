window.AMap = (function() {
  // 高德地图核心功能
  class AMapClass {
    constructor(options) {
      this.container = document.getElementById(options.container || 'map')
      this.zoom = options.zoom || 13
      this.center = options.center || [116.397428, 39.90923]
      this.markers = []
      this.init()
    }

    init() {
      // 初始化地图容器样式
      if (this.container) {
        this.container.style.width = '100%'
        this.container.style.height = '100%'
      }
    }

    // 添加标记
    addMarker(marker) {
      this.markers.push(marker)
      marker.setMap(this)
      return marker
    }

    // 移除标记
    removeMarker(marker) {
      const index = this.markers.indexOf(marker)
      if (index > -1) {
        this.markers.splice(index, 1)
        marker.setMap(null)
      }
    }

    // 清除所有标记
    clearMarkers() {
      this.markers.forEach(marker => marker.setMap(null))
      this.markers = []
    }

    // 设置中心点
    setCenter(position) {
      this.center = position
    }

    // 设置缩放级别
    setZoom(zoom) {
      this.zoom = zoom
    }
  }

  // 标记类
  class Marker {
    constructor(options) {
      this.position = options.position
      this.map = null
      this.icon = options.icon
      this.title = options.title
      this.content = options.content
      this.events = {}
    }

    setMap(map) {
      this.map = map
    }

    on(eventName, callback) {
      this.events[eventName] = callback
    }

    remove() {
      if (this.map) {
        this.map.removeMarker(this)
      }
    }
  }

  // 信息窗体类
  class InfoWindow {
    constructor(options) {
      this.content = options.content
      this.offset = options.offset
      this.map = null
      this.position = null
    }

    open(map, position) {
      this.map = map
      this.position = position
    }
  }

  // 像素坐标类
  class Pixel {
    constructor(x, y) {
      this.x = x
      this.y = y
    }
  }

  // 尺寸类
  class Size {
    constructor(width, height) {
      this.width = width
      this.height = height
    }
  }

  // 图标类
  class Icon {
    constructor(options) {
      this.size = options.size
      this.imageSize = options.imageSize
      this.imageOffset = options.imageOffset
      this.image = options.image
    }
  }

  return {
    Map: AMapClass,
    Marker: Marker,
    InfoWindow: InfoWindow,
    Pixel: Pixel,
    Size: Size,
    Icon: Icon
  }
})(); 