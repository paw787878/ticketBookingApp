#!/bin/bash

curl -H "Content-Type: application/json" 'http://localhost:8080/api/screenings?minimalStartTime=2100-01-01T07:00:00.000%2B00:00&maximalEndTime=2100-01-02T23:40:00.000%2B00:00&offset=0&limit=1000' | json_pp

echo ''
echo ''

curl -H "Content-Type: application/json" 'http://localhost:8080/api/screening?screeningId=1' | json_pp

echo ''
echo ''

curl -H "Content-Type: application/json" -d "@reservationRequest.json" -X POST 'http://localhost:8080/api/createReservation' | json_pp

echo ''
echo ''
