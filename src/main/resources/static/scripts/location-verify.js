let map;
    let circle;  // 지도 위 원 (반경 1km)
    let dongCenter; // 동 중심 좌표 (원 중심 좌표)

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

    function extractDong(address) {
      const match = address.match(/([가-힣]+동)/);
      return match ? match[1] : null;
    }

    function getCenterOfDong(dongName, callback) {
      const geocoder = new google.maps.Geocoder();
      geocoder.geocode({ address: dongName }, (results, status) => {
        if (status === "OK" && results[0]) {
          const center = results[0].geometry.location;
          callback(center);
        } else {
          console.error("동 중심 위치를 찾을 수 없습니다.", status);
          callback(null);
        }
      });
    }

    function parseAddressComponents(components) {
      let province = "";
      let district = "";
      let neighborhood = "";

      components.forEach((comp) => {
        if (comp.types.includes("administrative_area_level_1")) {
          province = comp.long_name;
        } else if (comp.types.includes("sublocality_level_1")) {
          district = comp.long_name;
        } else if (comp.types.includes("sublocality_level_2")) {
          neighborhood = comp.long_name;
        }
      });

      return province + " " + district + " " + neighborhood;
    }

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
                const dong = extractDong(results[0].formatted_address);

                if (address) {
                  document.getElementById("dong-name").textContent = parseAddressComponents(address);
                  document.getElementById("userCurrentAddress").value = parseAddressComponents(address);

                  getCenterOfDong(dong, (dongCenter) => {
                    if (dongCenter) {
                      new google.maps.Marker({
                        position: dongCenter,
                        map: map,
                        title: dong + " 중심 위치",
                      });

                      circle = new google.maps.Circle({
                          strokeWeight: 2,
                          fillColor: '#0000FF',
                          fillOpacity: 0.1,
                          map: map,
                          center: dongCenter,
                          radius: 1500
                        });
                      map.setCenter(dongCenter);

                      document.getElementById("verifyForm").style.visibility="visible"
                      document.getElementById("loadingElement").style.visibility="hidden"
                    }
                  });
                } else {
                  document.getElementById("dong-name").textContent = "동 정보를 찾을 수 없습니다.";
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
            document.querySelector("button[type='submit']").disabled = false;
            document.getElementById("location-match-message").style.visibility = "visible";
          } else {
            alert("검색한 위치가 인증 반경 밖입니다.");
            document.querySelector("button[type='submit']").disabled = true;
            document.getElementById("location-match-message").style.visibility = "hidden";
          }
        } else {
          alert("주소를 찾을 수 없습니다. 다시 입력해주세요.");
          document.querySelector("button[type='submit']").disabled = false;
          document.getElementById("location-match-message").style.visibility = "hidden";
        }
      });
    }

    document.addEventListener("DOMContentLoaded", () => {
      initMap();
    });