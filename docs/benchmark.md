### Using wrk
    wrk -t <số luồng> -c <số kết nối> -d <thời gian> <URL>

- -t: Số luồng (threads) thực hiện gửi yêu cầu.
- -c: Số kết nối (connections) đồng thời.
- -d: Thời gian chạy benchmark (ví dụ: 10s, 1m).
- <URL>: URL cần benchmark.

Ket qua
- Latency: Độ trễ trung bình, trung vị, và cao nhất.
- Requests/sec: Số yêu cầu được xử lý mỗi giây.
- Transfer/sec: Tổng lượng dữ liệu được truyền mỗi giây.

### Using echo and vegeta
     echo "GET http://localhost:1122/ticket/1/detail/1" | vegeta attack -name=2000qps -duration=10s -rate=100 | tee benchmark/results_2000qps.bin | vegeta report

### 