package UDP;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

/*
 * ============================================================================
 * FILE TỔNG HỢP BÀI TẬP UDP
 * Thứ tự nội dung: Customer, Student, client1, client2, ..., client6.
 *
 * Lưu ý: Các lớp không khai báo public để có thể đặt chung trong một file Java.
 * Muốn chạy client nào, biên dịch file này rồi gọi đúng tên lớp client đó.
 * ============================================================================
 */

// ============================================================================
// PHẦN 1: LỚP DỮ LIỆU CUSTOMER (từ file Customer.java) phai cho vao package UDP de chay client6
// ============================================================================
class Customer implements Serializable {
    private static final long serialVersionUID = 20151107L;

    public String id;
    public String code;
    public String name;
    public String dayOfBirth;
    public String userName;

    public Customer(String id, String code, String name,
                    String dayOfBirth, String userName) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.dayOfBirth = dayOfBirth;
        this.userName = userName;
    }
}

// ============================================================================
// PHẦN 2: LỚP DỮ LIỆU STUDENT (từ file Student.java) phai cho vao package UDP de chay client5 va client6
// ============================================================================
class Student implements Serializable {
    private static final long serialVersionUID = 20171107L;

    public String id;
    public String code;
    public String name;
    public String email;

    public Student(String id, String code, String name, String email) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.email = email;
    }

    public Student(String code) {
        this.code = code;
    }
}

// ============================================================================
// PHẦN 3: CLIENT 1 - TÌM CÁC SỐ CÒN THIẾU TRONG ĐOẠN TỪ 1 ĐẾN N
// Server UDP: 36.50.135.242:2207
// ============================================================================
class client1 {
    public static void main(String[] args) throws Exception {
        InetAddress server = InetAddress.getByName("36.50.135.242");
        int port = 2207;

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            // Bước 1: Gửi mã sinh viên và mã câu hỏi.
            String message = ";B23DCCN699;6b3gf4QG";
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(data, data.length, server, port));

            // Bước 2: Nhận dữ liệu dạng requestId;n;A1,A2,...,Am.
            byte[] buffer = new byte[4096];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);

            String received = new String(
                    response.getData(), response.getOffset(),
                    response.getLength(), StandardCharsets.UTF_8
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

            // Bước 3: Tìm và gửi danh sách các số còn thiếu.
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
        } // Bước 4: Socket tự động đóng.
    }
}

// ============================================================================
// PHẦN 4: CLIENT 2 - TÌM GIÁ TRỊ LỚN NHẤT VÀ NHỎ NHẤT
// Server UDP: 36.50.135.242:2207
// ============================================================================
class client2 {
    public static void main(String[] args) throws Exception {
        InetAddress server = InetAddress.getByName("36.50.135.242");
        int port = 2207;

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            // Bước 1: Gửi mã sinh viên và mã câu hỏi.
            String message = ";B23DCCN699;CBtGjjxh";
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(data, data.length, server, port));

            // Bước 2: Nhận dữ liệu dạng requestId;a1,a2,...,a50.
            byte[] buffer = new byte[4096];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);

            String received = new String(
                    response.getData(), response.getOffset(),
                    response.getLength(), StandardCharsets.UTF_8
            ).trim();
            System.out.println("Nhan: " + received);

            String[] parts = received.split(";", 2);
            String requestId = parts[0];
            String[] numbers = parts[1].split(",");

            // Bước 3: Tìm max, min rồi gửi kết quả.
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
        } // Bước 4: Socket tự động đóng.
    }
}

// ============================================================================
// PHẦN 5: CLIENT 3 - TÌM KÝ TỰ XUẤT HIỆN NHIỀU NHẤT VÀ CÁC VỊ TRÍ
// Server UDP: 36.50.135.242:2208
// ============================================================================
class client3 {
    public static void main(String[] args) throws Exception {
        InetAddress server = InetAddress.getByName("36.50.135.242");
        int port = 2208;

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            // Bước 1: Gửi mã sinh viên và mã câu hỏi.
            String message = ";B23DCCN699;zZBB0Cet";
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(data, data.length, server, port));

            // Bước 2: Nhận dữ liệu dạng requestId;data.
            byte[] buffer = new byte[65507];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String received = new String(
                    packet.getData(), packet.getOffset(),
                    packet.getLength(), StandardCharsets.UTF_8
            );
            System.out.println("Nhan: " + received);

            String[] parts = received.split(";", 2);
            String requestId = parts[0];
            String input = parts[1];

            // Đếm số lần xuất hiện của từng ký tự, giữ thứ tự xuất hiện.
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

            // Bước 3: Liệt kê vị trí từ 1 và gửi kết quả.
            StringBuilder positions = new StringBuilder();
            for (int i = 0; i < input.length(); i++) {
                if (input.charAt(i) == selected) {
                    positions.append(i + 1).append(",");
                }
            }

            String result = requestId + ";" + selected + ":" + positions;
            byte[] answer = result.getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(answer, answer.length, server, port));
            System.out.println("Gui: " + result);
        } // Bước 4: Socket tự động đóng.
    }
}

// ============================================================================
// PHẦN 6: CLIENT 4 - CHUẨN HÓA CHỮ CÁI ĐẦU CỦA MỖI TỪ
// Server UDP: 36.50.135.242:2208
// ============================================================================
class client4 {
    public static void main(String[] args) throws Exception {
        InetAddress server = InetAddress.getByName("36.50.135.242");
        int port = 2208;

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            // Bước 1: Gửi mã sinh viên và mã câu hỏi.
            String message = ";B23DCCN699;f6HWu5ox";
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(data, data.length, server, port));

            // Bước 2: Nhận dữ liệu dạng requestId;data.
            byte[] buffer = new byte[65507];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);

            String received = new String(
                    response.getData(), response.getOffset(),
                    response.getLength(), StandardCharsets.UTF_8
            );
            System.out.println("Nhan: " + received);

            String[] parts = received.split(";", 2);
            String requestId = parts[0];
            String input = parts[1].toLowerCase(Locale.ROOT);

            // Bước 3: Viết hoa ký tự đầu mỗi từ, giữ nguyên khoảng trắng.
            StringBuilder normalized = new StringBuilder();
            boolean startOfWord = true;
            for (int i = 0; i < input.length();) {
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
            socket.send(new DatagramPacket(answer, answer.length, server, port));
            System.out.println("Gui: " + result);
        } // Bước 4: Socket tự động đóng.
    }
}

// ============================================================================
// PHẦN 7: CLIENT 5 - CHUẨN HÓA ĐỐI TƯỢNG STUDENT VÀ TẠO EMAIL
// Server UDP: 36.50.135.242:2209
// ============================================================================
class client5 {
    public static void main(String[] args) throws Exception {
        InetAddress server = InetAddress.getByName("36.50.135.242");
        int port = 2209;

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            // Bước 1: Gửi mã sinh viên và mã câu hỏi.
            byte[] message = ";B23DCCN699;Lidiso2P"
                    .getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(
                    message, message.length, server, port
            ));

            // Bước 2: Nhận 8 byte requestId và đối tượng Student.
            byte[] buffer = new byte[65507];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);

            if (response.getLength() <= 8) {
                throw new IOException("Du lieu nhan khong hop le");
            }

            int offset = response.getOffset();
            byte[] requestId = Arrays.copyOfRange(
                    response.getData(), offset, offset + 8
            );

            Student student;
            try (ObjectInputStream input = new ObjectInputStream(
                    new ByteArrayInputStream(
                            response.getData(), offset + 8,
                            response.getLength() - 8
                    ))) {
                student = (Student) input.readObject();
            }
            System.out.println("Ten nhan: " + student.name);

            // Bước 3a: Chuẩn hóa tên - viết hoa chữ đầu mỗi từ.
            String[] words = student.name.trim()
                    .toLowerCase(Locale.ROOT)
                    .split("\\s+");
            StringBuilder normalizedName = new StringBuilder();
            for (String word : words) {
                if (normalizedName.length() > 0) {
                    normalizedName.append(" ");
                }
                normalizedName.append(
                        word.substring(0, 1).toUpperCase(Locale.ROOT)
                ).append(word.substring(1));
            }
            student.name = normalizedName.toString();

            // Bước 3b: Tạo email = tên + chữ đầu của họ và tên đệm.
            String plainName = Normalizer.normalize(
                    student.name, Normalizer.Form.NFD
            ).replaceAll("\\p{M}+", "")
             .replace('đ', 'd')
             .replace('Đ', 'D')
             .toLowerCase(Locale.ROOT);

            String[] emailWords = plainName.split("\\s+");
            StringBuilder email = new StringBuilder(
                    emailWords[emailWords.length - 1]
            );
            for (int i = 0; i < emailWords.length - 1; i++) {
                email.append(emailWords[i].charAt(0));
            }
            student.email = email + "@ptit.edu.vn";

            // Bước 4: Gửi 8 byte requestId và Student đã xử lý.
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write(requestId);
            try (ObjectOutputStream objectOutput =
                         new ObjectOutputStream(output)) {
                objectOutput.writeObject(student);
                objectOutput.flush();
            }

            byte[] answer = output.toByteArray();
            socket.send(new DatagramPacket(answer, answer.length, server, port));
            System.out.println("Ten gui: " + student.name);
            System.out.println("Email gui: " + student.email);
        } // Bước 5: Socket tự động đóng.
    }
}

// ============================================================================
// PHẦN 8: CLIENT 6 - CHUẨN HÓA ĐỐI TƯỢNG CUSTOMER
// Server UDP: 36.50.135.242:2209
// ============================================================================
class client6 {
    public static void main(String[] args) throws Exception {
        InetAddress server = InetAddress.getByName("36.50.135.242");
        int port = 2209;

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            // Bước 1: Gửi mã sinh viên và mã câu hỏi.
            byte[] message = ";B23DCCN699;KgPdd6pP"
                    .getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(
                    message, message.length, server, port
            ));

            // Bước 2: Nhận 8 byte requestId và đối tượng Customer.
            byte[] buffer = new byte[65507];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);

            if (response.getLength() <= 8) {
                throw new IOException("Du lieu nhan khong hop le");
            }

            int offset = response.getOffset();
            byte[] requestId = Arrays.copyOfRange(
                    response.getData(), offset, offset + 8
            );

            Customer customer;
            try (ObjectInputStream input = new ObjectInputStream(
                    new ByteArrayInputStream(
                            response.getData(), offset + 8,
                            response.getLength() - 8
                    ))) {
                customer = (Customer) input.readObject();
            }

            // Bước 3a: Tách các từ từ tên ban đầu.
            String[] words = customer.name.trim()
                    .toLowerCase(Locale.ROOT)
                    .split("\\s+");
            int last = words.length - 1;

            // Bước 3b: Tạo tài khoản = chữ đầu họ/tên đệm + tên.
            StringBuilder userName = new StringBuilder();
            for (int i = 0; i < last; i++) {
                userName.append(words[i].charAt(0));
            }
            userName.append(words[last]);
            customer.userName = userName.toString();

            // Bước 3c: Chuẩn hóa tên, ví dụ DUONG, Nguyen Van Hai.
            StringBuilder formattedName = new StringBuilder(
                    words[last].toUpperCase(Locale.ROOT)
            );
            if (last > 0) {
                formattedName.append(", ");
                for (int i = 0; i < last; i++) {
                    if (i > 0) {
                        formattedName.append(" ");
                    }
                    formattedName.append(
                            words[i].substring(0, 1)
                                    .toUpperCase(Locale.ROOT)
                    ).append(words[i].substring(1));
                }
            }
            customer.name = formattedName.toString();

            // Bước 3d: Đổi ngày sinh từ mm-dd-yyyy thành dd/mm/yyyy.
            String[] date = customer.dayOfBirth.trim().split("-");
            customer.dayOfBirth = date[1] + "/" + date[0] + "/" + date[2];

            // Bước 4: Gửi 8 byte requestId và Customer đã xử lý.
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write(requestId);
            try (ObjectOutputStream objectOutput =
                         new ObjectOutputStream(output)) {
                objectOutput.writeObject(customer);
                objectOutput.flush();
            }

            byte[] answer = output.toByteArray();
            socket.send(new DatagramPacket(answer, answer.length, server, port));
            System.out.println("Name: " + customer.name);
            System.out.println("Day of birth: " + customer.dayOfBirth);
            System.out.println("Username: " + customer.userName);
        } // Bước 5: Socket tự động đóng.
    }
}
