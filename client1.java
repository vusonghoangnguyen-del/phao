// Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2207. Yêu cầu là xây dựng một chương trình client trao đổi thông tin với server theo kịch bản:

// a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng ;studentCode;qCode.

// Ví dụ: ;B15DCCN001;73457A17

// b. Nhận thông điệp là một chuỗi từ server theo định dạng requestId;n;A1,A2,...An , với

// requestId là chuỗi ngẫu nhiên duy nhất
// n là một số ngẫu nhiên nhỏ hơn 100.
// A1, A2 ... Am (m <= n) là các giá trị ngẫu nhiên nhỏ hơn hoặc bằng n và có thể trùng nhau.
// Ví dụ: requestId;10;2,3,5,6,5

// c. Tìm kiếm các giá trị còn thiếu và gửi lên server theo định dạng requestId;B1,B2,...,Bm

// Ví dụ: requestId;1,4,7,8,9,10

// d. Đóng socket và kết thúc chương trình.

package UDP;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

public class client1 {
    public static void main(String[] args) throws Exception {
        InetAddress server = InetAddress.getByName("36.50.135.242");
        int port = 2207;

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            // a. Gửi mã sinh viên và mã câu hỏi
            String message = ";B23DCCN699;6b3gf4QG";
            byte[] data = message.getBytes(StandardCharsets.UTF_8);

            socket.send(new DatagramPacket(
                data, data.length, server, port
            ));

            // b. Nhận dữ liệu từ server
            byte[] buffer = new byte[4096];
            DatagramPacket response =
                new DatagramPacket(buffer, buffer.length);
            socket.receive(response);

            String received = new String(
                response.getData(),
                response.getOffset(),
                response.getLength(),
                StandardCharsets.UTF_8
            ).trim();

            System.out.println("Nhan: " + received);

            String[] parts = received.split(";", -1);
            String requestId = parts[0];
            int n = Integer.parseInt(parts[1].trim());

            boolean[] appeared = new boolean[n + 1];

            if (!parts[2].trim().isEmpty()) {
                for (String item : parts[2].split(",")) {
                    int value = Integer.parseInt(item.trim());
                    if (value >= 1 && value <= n) {
                        appeared[value] = true;
                    }
                }
            }

            // c. Tìm các số còn thiếu trong khoảng 1 đến n
            StringJoiner missing = new StringJoiner(",");
            for (int i = 1; i <= n; i++) {
                if (!appeared[i]) {
                    missing.add(String.valueOf(i));
                }
            }

            String result = requestId + ";" + missing;
            byte[] resultData = result.getBytes(StandardCharsets.UTF_8);

            socket.send(new DatagramPacket(
                resultData, resultData.length, server, port
            ));

            System.out.println("Gui: " + result);
        } // d. Tự động đóng socket
    }
}