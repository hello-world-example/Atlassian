package xyz.kail.demo.leader.atlassian

class VO {
    VO(int sort, String summary, Date timestamp) {
        this.sort = sort
        this.summary = summary
        this.timestamp = timestamp
    }
    int sort;
    String summary;
    Date timestamp;
}

List<VO> list = [
        new VO(123, ".123", new Date()),
        new VO(234, "234", new Date()),
        new VO(456, "456", new Date()),
] as List

def order = list.findAll { it -> !it.summary.contains("123") }
        .collect { it -> it.sort }
        .min()

println(order.class)
println(order)

