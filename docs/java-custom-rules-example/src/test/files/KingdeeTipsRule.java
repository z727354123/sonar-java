package kd.hrmp.hbos.formplugin.web;


import kd.bos.dataentity.resource.ResManager;

public class KingdeeTipsRule {

  private static final String TEST_STRING = ResManager.loadKDString("您负责的%s状态已更新为完成", "InitPlanServiceHelper_4", "test");// Noncompliant

  public void main(String[] args, String name) {
    String.format(ResManager.loadKDString("您负责的%s状态已更新为%s", "InitPlanServiceHelper_4", "test"), "ewe", "完成");// Noncompliant
    String.format(ResManager.loadKDString(" 您负责的%s状态已更新为完成 ", "InitPlanServiceHelper_4", "test"), "ewe", "完成");// Noncompliant
    String.format(ResManager.loadKDString("您负责的%s状态已更新为完成 ", "InitPlanServiceHelper_4", "test"), "ewe", "完成");// Noncompliant
    String.format(ResManager.loadKDString(" 您负责的%s状态已更新为完成", "InitPlanServiceHelper_4", "test"), "ewe", "完成");// Noncompliant
    String.format(ResManager.loadKDString("您负责\n的%s状态已更新为完成", "InitPlanServiceHelper_4", "test"), "ewe", "完成");// Noncompliant
    String.format(ResManager.loadKDString("您负责" + "\n" + "的%s状态已更新为完成", "InitPlanServiceHelper_4", "test"), "ewe", "完成");
    String.format(TEST_STRING, "ewe", "完成");
    String.format(ResManager.loadKDString("您负责的%1$s状态已更新为%2$s", "InitPlanServiceHelper_4", "test"), "ewe", "完成");
  }
}
