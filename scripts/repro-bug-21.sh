#!/usr/bin/env bash
# Reproduction du bug #21 : 15 essais de deux présences simultanées avec un exercice en attente
F=${F:-http://localhost:3000/api}; H=Content-Type:application/json; T=$(mktemp -d); perdu=0
for i in $(seq 1 15); do
  S=$(curl -s -H $H -d "{\"titre\":\"Repro $i\",\"promotionId\":1}" $F/sessions)
  CODE=$(echo $S | python3 -c "import sys,json;print(json.load(sys.stdin)['code'])")
  SID=$(echo $S | python3 -c "import sys,json;print(json.load(sys.stdin)['id'])")
  curl -s -o /dev/null -H $H -d "{\"sessionId\":$SID,\"etudiantId\":1,\"lien\":\"https://github.com/awa/r$i\"}" $F/exercices
  curl -s -o /dev/null -w '%{http_code}' -H $H -d "{\"code\":\"$CODE\",\"etudiantId\":2}" $F/presences > $T/a &
  curl -s -o /dev/null -w '%{http_code}' -H $H -d "{\"code\":\"$CODE\",\"etudiantId\":3}" $F/presences > $T/b &
  wait
  [ "$(cat $T/a) $(cat $T/b)" != "201 201" ] && perdu=$((perdu+1))
done
echo "présences perdues : $perdu / 15"
