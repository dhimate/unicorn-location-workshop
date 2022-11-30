curl -XPOST \
  --url ${API_GW_URL} \
  --header 'content-type: application/json' \
  --data '{
  "unicornName": "John",
  "longitude": "13.404954",
  "latitude": "52.520008"
}' 
