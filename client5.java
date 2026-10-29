// Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2209. Yêu cầu là xây dựng một chương trình client trao đổi thông tin với server theo kịch bản sau: Đối tượng trao đổi là thể hiện của lớp UDP.Student được mô tả:

// Tên đầy đủ lớp: UDP.Student
// Các thuộc tính: id String,code String, name String, email String
// 02 Hàm khởi tạo:
// public Student(String id, String code, String name, String email)
// public Student(String code)
// Trường dữ liệu: private static final long serialVersionUID = 20171107
// Thực hiện:

// a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng ;studentCode;qCode.

// Ví dụ: ;B15DCCN001;EE29C059

// b. Nhận thông điệp chứa: 08 byte đầu chứa chuỗi requestId, các byte còn lại chứa một đối tượng là thể hiện của lớp Student từ server. Trong đó, các thông tin được thiết lập gồm id và name.

// c. Yêu cầu:

// Chuẩn hóa tên theo quy tắc: Chữ cái đầu tiên in hoa, các chữ cái còn lại in thường và gán lại thuộc tính name của đối tượng
// Tạo email ptit.edu.vn từ tên người dùng bằng cách lấy tên và các chữ cái bắt đầu của họ và tên đệm.
// Ví dụ: nguyen van tuan nam -> namnvt@ptit.edu.vn. Gán giá trị này cho thuộc tính email của đối tượng nhận được

// Gửi thông điệp chứa đối tượng xử lý ở bước c lên Server với cấu trúc: 08 byte đầu chứa chuỗi requestId và các byte còn lại chứa đối tượng Student đã được sửa đổi.
// d. Đóng socket và kết thúc chương trình.

package UDP;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;

import UDP.Student;

public class client5 {
    public static void main(String[] args) throws Exception {
        InetAddress server = InetAddress.getByName("36.50.135.242");
        int port = 2209;

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            // a. Gửi mã sinh viên và mã câu hỏi
            byte[] message = ";B23DCCN699;Lidiso2P"
                    .getBytes(StandardCharsets.UTF_8);

            socket.send(new DatagramPacket(
                    message, message.length, server, port
            ));

            // b. Nhận 8 byte requestId và đối tượng Student
            byte[] buffer = new byte[65507];
            DatagramPacket response =
                    new DatagramPacket(buffer, buffer.length);
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
                            response.getData(),
                            offset + 8,
                            response.getLength() - 8
                    ))) {
                student = (Student) input.readObject();
            }

            System.out.println("Ten nhan: " + student.name);

            // c. Chuẩn hóa: viết hoa chữ đầu mỗi từ
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

            // Email: tên + chữ đầu của họ và tên đệm
            String plainName = Normalizer.normalize(
                    student.name, Normalizer.Form.NFD
            ).replaceAll("\\p{M}+", "")
             .replace('đ', 'd')
             .replace('Đ', 'D')
             .toLowerCase(Locale.ROOT);

            String[] emailWords = plainName.split("\\s+");
            StringBuilder email =
                    new StringBuilder(emailWords[emailWords.length - 1]);

            for (int i = 0; i < emailWords.length - 1; i++) {
                email.append(emailWords[i].charAt(0));
            }

            student.email = email + "@ptit.edu.vn";

            // Giữ nguyên 8 byte requestId, sau đó ghi đối tượng
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write(requestId);

            try (ObjectOutputStream objectOutput =
                         new ObjectOutputStream(output)) {
                objectOutput.writeObject(student);
                objectOutput.flush();
            }

            byte[] answer = output.toByteArray();
            socket.send(new DatagramPacket(
                    answer, answer.length, server, port
            ));

            System.out.println("Ten gui: " + student.name);
            System.out.println("Email gui: " + student.email);
        } // d. Đóng socket
    }
}