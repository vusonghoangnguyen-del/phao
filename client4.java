// Một chương trình server cho phép kết nối qua giao thức UDP tại cổng 2208. Yêu cầu là xây dựng một chương trình client trao đổi thông tin với server theo kịch bản dưới đây:

// a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng ;studentCode;qCode.

// Ví dụ: ;B15DCCN001;5B35BCC1

// b. Nhận thông điệp từ server theo định dạng requestId;data

// requestId là một chuỗi ngẫu nhiên duy nhất
// data là chuỗi dữ liệu cần xử lý
// c. Xử lý chuẩn hóa chuỗi đã nhận thành theo nguyên tắc

// Ký tự đầu tiên của từng từ trong chuỗi là in hoa
// Các ký tự còn lại của chuỗi là in thường Gửi thông điệp chứa chuỗi đã được chuẩn hóa lên server theo định dạng requestId;data
// d. Đóng socket và kết thúc chương trình

package UDP;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class client4 {
    public static void main(String[] args) throws Exception {
        InetAddress server = InetAddress.getByName("36.50.135.242");
        int port = 2208;

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            // a. Gửi mã sinh viên và mã câu hỏi
            String message = ";B23DCCN699;f6HWu5ox";
            byte[] data = message.getBytes(StandardCharsets.UTF_8);

            socket.send(new DatagramPacket(
                data, data.length, server, port
            ));

            // b. Nhận requestId;data
            byte[] buffer = new byte[65507];
            DatagramPacket response =
                new DatagramPacket(buffer, buffer.length);
            socket.receive(response);

            String received = new String(
                response.getData(),
                response.getOffset(),
                response.getLength(),
                StandardCharsets.UTF_8
            );

            System.out.println("Nhan: " + received);

            String[] parts = received.split(";", 2);
            String requestId = parts[0];
            String input = parts[1].toLowerCase(Locale.ROOT);

            // c. Viết hoa ký tự đầu mỗi từ, giữ nguyên khoảng trắng
            StringBuilder normalized = new StringBuilder();
            boolean startOfWord = true;

            for (int i = 0; i < input.length(); ) {
                int c = input.codePointAt(i);

                if (Character.isWhitespace(c)) {
                    normalized.appendCodePoint(c);
                    startOfWord = true;
                } else {
                    normalized.appendCodePoint(
                        startOfWord ? Character.toUpperCase(c) : c
                    );
                    startOfWord = false;
                }

                i += Character.charCount(c);
            }

            String result = requestId + ";" + normalized;
            byte[] answer = result.getBytes(StandardCharsets.UTF_8);

            socket.send(new DatagramPacket(
                answer, answer.length, server, port
            ));

            System.out.println("Gui: " + result);
        } // d. Tự động đóng socket
    }
}