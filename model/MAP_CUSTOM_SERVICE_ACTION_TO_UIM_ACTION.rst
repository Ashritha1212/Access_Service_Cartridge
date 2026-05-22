<?xml version="1.0" encoding="UTF-8"?>
<com:modelEntity xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:com="http://www.mslv.com/studio/core/model/common" xmlns:inv="http://www.mslv.com/studio/inventory/model/specification" xmlns="http://www.mslv.com/studio/inventory/model/specification" xsi:type="inv:RuleTriggerType" name="MAP_CUSTOM_SERVICE_ACTION_TO_UIM_ACTION">
  <com:saveVersion>3</com:saveVersion>
  <com:id>vBHwgkizR1G8PHElYEHuHA</com:id>
  <inv:placement>INSTEAD</inv:placement>
  <inv:point>
    <com:entity>BusinessInteractionManager_getEntityAction</com:entity>
    <com:entityType>rstp</com:entityType>
    <com:relationship>com.mslv.studio.inventory.ruleset.trigger.REL_POINT</com:relationship>
  </inv:point>
  <inv:ruleset>
    <com:entity>MAP_SERVICE_ACTION</com:entity>
    <com:entityType>ruleset</com:entityType>
    <com:relationship>com.mslv.studio.inventory.ruleset.trigger.REL_RULESET</com:relationship>
  </inv:ruleset>
</com:modelEntity>