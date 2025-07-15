let map; //맵
let circle;  // 지도 위 원
let circle_radius = 1500; // 현재 위치 기준 허용할 반경(km 단위)

//권한 체크
window.onload = function () {
  if (navigator.permissions) {
    navigator.permissions.query({ name: "geolocation" }).then((result) => {
      if (result.state === "denied" || result.state === "prompt") {
        window.location.href = "/maps/permission";
      }
    });
  } else {
    window.location.href = "/maps/permission";
  }
};

//지도 스트링 > 시 군/구 동 정보만 추출
function extractAddress(components) {
  let simplifyAddress = "";

  components.slice(1,-1).forEach((component)=>{
    simplifyAddress = component.long_name + " " + simplifyAddress;
  });

  return simplifyAddress.trim();
}

//맵 초기화 함수
function initMap() {
  const defaultPos = { lat: 37.5665, lng: 126.9780 };

  map = new google.maps.Map(document.getElementById("map"), {
    center: defaultPos,
    zoom: 14,
    disableDefaultUI: true,
    draggable: false,
    scrollwheel: false,
    disableDoubleClickZoom: true,
    gestureHandling: "none",
  });

  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const currentPos = {
          lat: position.coords.latitude,
          lng: position.coords.longitude,
        };

        const geocoder = new google.maps.Geocoder();
        geocoder.geocode({ location: currentPos }, (results, status) => {
          if (status === "OK" && results[0]) {
            const address = results[0].address_components;

            if (address) {
              document.getElementById("dong-name").textContent = extractAddress(address);
              document.getElementById("userCurrentAddress").value = extractAddress(address);

              new google.maps.Marker({
                position: currentPos,
                map: map,
                title: "현재 위치",
              });

              circle = new google.maps.Circle({
                strokeWeight: 0,
                fillColor: '#0000FF',
                fillOpacity: 0.1,
                map: map,
                center: currentPos,
                radius: circle_radius,
              });
              map.setCenter(currentPos);

              document.getElementById("verifyForm").style.visibility="visible"
            } else {
              document.getElementById("dong-name").textContent = "주소 정보를 찾을 수 없습니다.";
            }
          } else {
            document.getElementById("dong-name").textContent = "주소 정보를 불러올 수 없습니다.";
          }
        });
      },
      (error) => {
        alert("위치 권한을 허용해 주세요.");
        window.location.href = "/maps/permission";
      }
    );
  } else {
    console.warn("브라우저가 위치 기능을 지원하지 않습니다.");
    window.location.href = "/maps/permission";
  }

  const input = document.getElementById("userAddress");
  const autocomplete = new google.maps.places.Autocomplete(input, {
    componentRestrictions: { country: "kr" },
  });

  autocomplete.addListener("place_changed", function () {
    const place = autocomplete.getPlace();
    console.log(place.formatted_address);
    console.log(place.geometry.location.lat(), place.geometry.location.lng());
  });
}

function setUserAddress() {
  const address = document.getElementById("userAddress").value;

  if (!address) {
    alert("주소를 입력해주세요.");
    return;
  }

  const geocoder = new google.maps.Geocoder();

  geocoder.geocode({ address: address }, function (results, status) {
    if (status === "OK" && results[0]) {
      const searchedLocation = results[0].geometry.location;

      const distance = google.maps.geometry.spherical.computeDistanceBetween(
        circle.getCenter(),
        searchedLocation
      );

      if (distance <= circle.getRadius()) {
        document.getElementById("setAddressButton").disabled = false;
        document.getElementById("location-match-message").style.visibility = "visible";
      } else {
        alert("검색한 위치가 인증 반경 밖입니다.");
        document.getElementById("setAddressButton").disabled = true;
        document.getElementById("location-match-message").style.visibility = "hidden";
      }
    } else {
      alert("주소를 찾을 수 없습니다. 다시 입력해주세요.");
      document.getElementById("setAddressButton").disabled = true;
      document.getElementById("location-match-message").style.visibility = "hidden";
    }
  });
}

document.addEventListener("DOMContentLoaded", () => {
  initMap();
});