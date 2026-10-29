// Một chương trình server cho phép kết nối qua giao thức UDP tại cổng 2208. Yêu cầu là xây dựng một chương trình client tương tác với server kịch bản dưới đây:

// a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng ;studentCode;qCode.

// Ví dụ: ;B15DCCN001;EE29C059

// b. Nhận thông điệp từ server theo định dạng requestId; data

// requestId là một chuỗi ngẫu nhiên duy nhất
// data là chuỗi dữ liệu đầu vào cần xử lý
// Ví dụ: requestId;Qnc8d5x78aldSGWWmaAAjyg3

// c. Tìm kiếm ký tự xuất hiện nhiều nhất trong chuỗi và gửi lên server theo định dạng “requestId;ký tự xuất hiện nhiều nhất: các vị trí xuất hiện ký tự đó”

// Ví dụ: requestId;8:4,9,

// d. Đóng socket và kết thúc chương trình

package UDP;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class client3 {
    public static void main(String[] args) throws Exception {
        InetAddress server = InetAddress.getByName("36.50.135.242");
        int port = 2208;

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            // a. Gửi thông tin sinh viên
            String message = ";B23DCCN699;zZBB0Cet";
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(
                data, data.length, server, port
            ));

            // b. Nhận dữ liệu
            byte[] buffer = new byte[65507];
            DatagramPacket packet =
                new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String received = new String(
                packet.getData(),
                packet.getOffset(),
                packet.getLength(),
                StandardCharsets.UTF_8
            );
            System.out.println("Nhan: " + received);

            String[] parts = received.split(";", 2);
            String requestId = parts[0];
            String input = parts[1];

            // Đếm số lần xuất hiện của từng ký tự
            Map<Character, Integer> counts = new LinkedHashMap<>();
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                counts.put(c, counts.getOrDefault(c, 0) + 1);
            }

            char selected = '\0';
            int maxCount = 0;

            for (Map.Entry<Character, Integer> entry : counts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    selected = entry.getKey();
                }
            }

            // c. Các vị trí tính từ 1, giữ dấu phẩy cuối
            StringBuilder positions = new StringBuilder();
            for (int i = 0; i < input.length(); i++) {
                if (input.charAt(i) == selected) {
                    positions.append(i + 1).append(",");
                }
            }

            String result = requestId + ";" + selected + ":" + positions;
            byte[] answer = result.getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(
                answer, answer.length, server, port
            ));

            System.out.println("Gui: " + result);
        } // d. Đóng socket
    }
}
