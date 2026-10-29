// Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2207. Yêu cầu là xây dựng một chương trình client trao đổi thông tin với server theo kịch bản:

// a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng ;studentCode;qCode.

// Ví dụ: ;B15DCCN001;DC73CA2E

// b. Nhận thông điệp là một chuỗi từ server theo định dạng requestId;a1,a2,...,a50

// requestId là chuỗi ngẫu nhiên duy nhất
// a1 -> a50 là 50 số nguyên ngẫu nhiên
// c. Thực hiện tìm giá trị lớn nhất và giá trị nhỏ nhất thông điệp trong a1 -> a50 và gửi thông điệp lên lên server theo định dạng requestId;max,min

// d. Đóng socket và kết thúc chương trình

package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class client2 {
    public static void main(String[] args) throws Exception {
        InetAddress server = InetAddress.getByName("36.50.135.242");
        int port = 2207;

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            // a. Gửi mã sinh viên và mã câu hỏi
            String message = ";B23DCCN699;CBtGjjxh";
            byte[] data = message.getBytes(StandardCharsets.UTF_8);

            socket.send(new DatagramPacket(
                data, data.length, server, port
            ));

            // b. Nhận requestId;a1,a2,...,a50
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

            String[] parts = received.split(";", 2);
            String requestId = parts[0];
            String[] numbers = parts[1].split(",");

            // c. Tìm giá trị lớn nhất và nhỏ nhất
            int max = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;

            for (String number : numbers) {
                int value = Integer.parseInt(number.trim());
                max = Math.max(max, value);
                min = Math.min(min, value);
            }

            String result = requestId + ";" + max + "," + min;
            byte[] resultData = result.getBytes(StandardCharsets.UTF_8);

            socket.send(new DatagramPacket(
                resultData, resultData.length, server, port
            ));

            System.out.println("Gui: " + result);
        } // d. Tự động đóng socket
    }
}