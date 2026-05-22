<?xml version="1.0" encoding="UTF-8"?>
<com:modelEntity xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:com="http://www.mslv.com/studio/core/model/common" xmlns:data="http://www.oracle.com/communications/studio/core/model/common/data" xmlns:inv="http://www.mslv.com/studio/inventory/model/specification" xmlns="http://www.mslv.com/studio/inventory/model/specification" xmlns:layout="http://xmlns.oracle.com/communications/sce/poms/model/layout" xmlns:poms="http://xmlns.oracle.com/communications/sce/poms/model/poms" xsi:type="inv:BusinessInteractionSpecificationType" name="AccessService_BI">
  <com:saveVersion>2</com:saveVersion>
  <com:baseEntityRef>
    <com:entity>BusinessInteraction</com:entity>
    <com:entityType>inventoryEntity</com:entityType>
    <com:relationship>unknown</com:relationship>
  </com:baseEntityRef>
  <com:id>+YEA-L+NS-+jhSJ1IyM-Xg</com:id>
  <data:dataElementNode virtual="true">
    <com:id>+YEA-L+NS-+jhSJ1IyM-Xg</com:id>
    <com:elementType>oracle.communications.studio.model.data.StudioModelDataElement</com:elementType>
    <data:name>AccessService_BI</data:name>
    <data:displayName lang="[default]">AccessService_BI</data:displayName>
    <data:primitiveType>none</data:primitiveType>
  </data:dataElementNode>
  <data:dataElementDetails xsi:type="data:dataElementCommonDetail">
    <com:id>HO4V2DomQT6ta3+7+w1N8Q</com:id>
    <com:elementType>oracle.communications.studio.model.data.StudioModelDataElementCommonDetails</com:elementType>
    <data:dataElementId>+YEA-L+NS-+jhSJ1IyM-Xg</data:dataElementId>
    <data:defaultValue></data:defaultValue>
    <data:key></data:key>
    <data:deprecated>false</data:deprecated>
    <data:sensitive>false</data:sensitive>
    <data:minLength>0</data:minLength>
    <data:maxLength>40</data:maxLength>
    <data:minMultiplicity>0</data:minMultiplicity>
    <data:maxMultiplicity>-1</data:maxMultiplicity>
  </data:dataElementDetails>
  <data:dataElementDetails xsi:type="layout:uiConfigurationType">
    <com:id>nhMnaIJQSZiRtXfLzrVu7w</com:id>
    <com:elementType>oracle.communications.sce.poms.model.data.LayoutDetailType</com:elementType>
    <data:dataElementId>+YEA-L+NS-+jhSJ1IyM-Xg</data:dataElementId>
    <layout:layout>
      <layout:page>Business Interaction Editor</layout:page>
      <layout:panel>Business Interaction Edit Panel</layout:panel>
    </layout:layout>
    <layout:layout>
      <layout:page>Business Interaction Summary</layout:page>
      <layout:panel>Business Interaction Summary Panel</layout:panel>
    </layout:layout>
    <layout:layout>
      <layout:page>Orchestration Request Editor</layout:page>
      <layout:panel>Orchestration Request Edit Panel</layout:panel>
    </layout:layout>
  </data:dataElementDetails>
  <inv:rule>
    <com:entity>VALIDATE_ACCESS_RULEEXTPOINT</com:entity>
    <com:entityType>rst</com:entityType>
    <com:relationship>com.mslv.studio.inventory.specification.REL_RULE_TRIGGER</com:relationship>
  </inv:rule>
  <inv:entityType>BusinessInteraction</inv:entityType>
</com:modelEntity>