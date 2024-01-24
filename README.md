# RandomWorld
### [마인크래프트 플러그인]
 
### 개발기간
 > 2022.12.27 ~ 2024.01.24 <br>
 (실개발기간 : 2022.12.27~2023.01.10, 2024.01.07~2024.01.24 약 1개월)
 
 ### 개발언어
 > Java
 
 ### 설명
 + 동기
   + 뭐든지 랜덤이면 어떨까? <br>
 + 기획
   + 줍는 모든 아이템을 무작위로 교체
   + 제작대에서 아이템을 제작하는 경우에도 무작위로 교체
   + 모루의 경우 인첸트는 유지하고 아이템의 형태만 교체
   + 인첸트 테이블의 경우 인첸트만 무작위로 교체
   + 각종 버프효과도 랜덤으로 변환
   + 유저가 무작위 아이템/버프/인첸트 목록을 지정할 수 있도록 함
   + 유저가 특정 아이템/버프/인첸트는 무작위 변환하지 않게 지정할 수 있도록 함. <br> <br>


  
 ### 명령어 <br>
 > /randomworld <add|remove|set> <player|entity|default> <entity_name> <event_name> <args....> <br>
 
특정 유저 또는 엔티티의 이벤트 설정을 변경한다. <br>

add - 현재 설정에 해당 효과를 추가합니다. <br>
remove - 현재 설정에 해당 효과를 제외합니다. <br>
set - 현재 설정으로 바꿉니다. <br>

예시) <br>
/randomworld set player Link_State AREA_CLOUD_EFFECT_EXCEPT BAD_OMEN WITHER POISON <br>
플레이어 Link_State가 잔류형 포션효과를 받을 경우 흉조, 시듦, 독은 제외한 나머지 포션효과중 랜덤으로 부여한다. <br>

/randomworld add entity IRON_GOLEM AREA_CLOUD_EFFECT_BAN REGENERATION, HASTE <br>
엔티티 철골렘이 잔류형 포션효과로 인해 받는 효과 중 재생, 성급함 효과는 랜덤한 효과를 바꾸지 않는다.(기존 설정에 추가) <br>

/randomworld set entity IRON_GOLEM WITHER_ROSE_MAX 33 <br>
엔티티 철골렘이 위더장미로 인해 받을 수 있는 무작위 버프 갯수를 33개로 설정한다. <br>

/randomworld set default default BEACON_EXCEPT CONDUIT_POWER <br>
신호기로부터 효과를 받을 경우 '전달체의 힘' 효과를 제외한 나머지 포션효과중 랜덤으로 부여한다. <br>
이 경우 신호기 이벤트를 따로 지정하지 않은 플레이어 또는 엔티티에 대해서만 적용됩니다. <br> <br>

 > /randomworld permission <player_name> <user|admin|super> <br>
 
 유저의 권한을 변경한다. <br>

 user - 언어변경 명령어만 사용할 수 있는 등급 <br>
 admin - 자기자신의 이벤트를 변경할 수 있는 등급 <br>
 super - 모든 유저/엔티티/공통 설정을 변경할 수 있는 등급 <br> <br>

 > /randomworld switch <player|entity|default> <entity_name|default> <event_name> <br>
 
 해당 이벤트가 활성화 되어있으면 비활성화, 비활성화 되어있으면 활성화한다. <br> <br>
 
 예시) <br>
 /randomworld switch player Link_State PICKUP <br>
 플레이어 Link_State가 주운 아이템을 랜덤으로 변환할지 켜거나 끈다. <br> <br>

 > /randomworld language <user_name> <language> <br>
 
 해당 플레이어의 언어를 변경한다. <br>
 언어파일은 플러그인폴더 내 lang폴더에 yml파일로 존재한다. <br> <br>
 
 > /randomworld setting <br>
 
이벤트 관련 설정을 다룰 수 있는 설정창을 연다. <br>
플레이어만 사용가능한 명령어. <br> <br>

  <br> <br> <br>

 
