package com.sf.monitor.controllers;

import com.sf.log.Logger;
import com.sf.monitor.Resources;
import com.sf.monitor.druid.DruidInfos;
import com.sf.monitor.utils.JsonValues;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/druid")
public class DruidController {
  private static final Logger log = new Logger(DruidController.class);
  private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

  @RequestMapping(method = RequestMethod.POST,
    value = "/emitter",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public
  @ResponseBody
  String writeMetrics(HttpEntity<String> httpEntity) {
    try {
      Resources.fetchers.druidFetcher.writeMetrics(httpEntity.getBody());
    } catch (Exception e) {
      log.error(e, "");
    }
    return "done";
  }

  @RequestMapping(method = RequestMethod.POST,
    value = "/trend_metrics",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public
  @ResponseBody
  DruidInfos.Result<List<DruidInfos.TaggedValues>> getTrendMetrics(HttpEntity<String> httpEntity) throws Exception {
    try {
      DruidInfos.MetricsParam param = Resources.jsonMapper.readValue(
        httpEntity.getBody(),
        DruidInfos.MetricsParam.class
      );
      param.fromDateTime = DateTime.parse(param.from, formatter);
      param.toDateTime = DateTime.parse(param.to, formatter);
      return Resources.druidInfos.getTrendData(param);
    } catch (Exception e) {
      log.error(e, "");
      return new DruidInfos.Result<List<DruidInfos.TaggedValues>>(null, false, "");
    }
  }

  @RequestMapping(method = RequestMethod.POST,
    value = "/latest_metrics",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public
  @ResponseBody
  DruidInfos.Result<List<DruidInfos.TaggedValues>> getLatestMetrics(HttpEntity<String> httpEntity) throws Exception {
    try {
      DruidInfos.MetricsParam param = Resources.jsonMapper.readValue(
        httpEntity.getBody(),
        DruidInfos.MetricsParam.class
      );
      return Resources.druidInfos.getLatestData(param);
    } catch (Exception e) {
      log.error(e, "");
      return new DruidInfos.Result<List<DruidInfos.TaggedValues>>(null, false, "");
    }
  }

  @RequestMapping("/realtime_nodes")
  public
  @ResponseBody
  List<JsonValues> getRealtimeNodes() {
    return Resources.druidInfos.getRealtimeNodes();
  }

  @RequestMapping("/historical_nodes")
  public
  @ResponseBody
  List<JsonValues> getHistoricalNodes() {
    return Resources.druidInfos.getHistoricalNodes();
  }

  @RequestMapping("/middle_manager_nodes")
  public
  @ResponseBody
  List<JsonValues> getMiddleManagerNodes() {
    return Resources.druidInfos.getMiddleManagerNodes();
  }

  @RequestMapping("/broker_nodes")
  public
  @ResponseBody
  Iterable<Map<String, Object>> getBrokerNodes() {
    return DruidInfos.AnnounceNode.toMaps(Resources.druidInfos.getBrokerNodes());
  }

  @RequestMapping("/overlord_nodes")
  public
  @ResponseBody
  Iterable<Map<String, Object>> getOverlordNodes() {
    return DruidInfos.AnnounceNode.toMaps(Resources.druidInfos.getOverlordNodes());
  }

  @RequestMapping("/coordinator_nodes")
  public
  @ResponseBody
  Iterable<Map<String, Object>> getCoordinatorNodes() {
    return DruidInfos.AnnounceNode.toMaps(Resources.druidInfos.getCoordinatorNodes());
  }

}
