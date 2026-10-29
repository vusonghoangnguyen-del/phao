// Thông tin khách hàng được yêu cầu thay đổi định dạng lại cho phù hợp với khu vực, cụ thể:

// a. Tên khách hàng cần được chuẩn hóa theo định dạng mới.

// Ví dụ: nguyen van hai duong -> DUONG, Nguyen Van Hai

// b. Ngày sinh của khách hàng đang ở dạng mm-dd-yyyy, cần được chuyển thành định dạng dd/mm/yyyy.

// Ví dụ: 10-11-2012 -> 11/10/2012

// c. Tài khoản khách hàng được tạo từ các chữ cái in thường được sinh tự động từ họ tên khách hàng.

// Ví dụ: nguyen van hai duong -> nvhduong

// Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2209. Yêu cầu là xây dựng một chương trình client giao tiếp với server theo mô tả sau:

// a. Đối tượng trao đổi là thể hiện của lớp UDP.Customer được mô tả như sau

// Tên đầy đủ của lớp: UDP.Customer
// Các thuộc tính: id String, code String, name String, , dayOfBirth String, userName String
// Một Hàm khởi tạo với đầy đủ các thuộc tính được liệt kê ở trên
// Trường dữ liệu: private static final long serialVersionUID = 20151107;
// b. Client giao tiếp với server theo các bước

// Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng ;studentCode;qCode.
// Ví dụ: ;B15DCCN001;EE29C059

// Nhận thông điệp chứa: 08 byte đầu chứa chuỗi requestId, các byte còn lại chứa một đối tượng là thể hiện của lớp Customer từ server. Trong đó, các thuộc tính id, code, name,dayOfBirth đã được thiết lập sẵn.
// Yêu cầu thay đổi thông tin các thuộc tính như yêu cầu ở trên và gửi lại đối tượng khách hàng đã được sửa đổi lên server với cấu trúc: 08 byte đầu chứa chuỗi requestId và các byte còn lại chứa đối tượng Customer đã được sửa đổi.
// Đóng socket và kết thúc chương trình.

package UDP;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

public class client6 {
    public static void main(String[] args) throws Exception {
        InetAddress server = InetAddress.getByName("36.50.135.242");
        int port = 2209;

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(10000);

            // 1. Gửi mã sinh viên và mã câu hỏi
            byte[] message = ";B23DCCN699;KgPdd6pP"
                    .getBytes(StandardCharsets.UTF_8);

            socket.send(new DatagramPacket(
                    message, message.length, server, port
            ));

            // 2. Nhận 8 byte requestId và đối tượng Customer
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

            Customer customer;

            try (ObjectInputStream input = new ObjectInputStream(
                    new ByteArrayInputStream(
                            response.getData(),
                            offset + 8,
                            response.getLength() - 8
                    ))) {
                customer = (Customer) input.readObject();
            }

            // 3. Tách các từ từ tên ban đầu
            String[] words = customer.name.trim()
                    .toLowerCase(Locale.ROOT)
                    .split("\\s+");

            int last = words.length - 1;

            // Tài khoản: chữ đầu họ, tên đệm + tên
            StringBuilder userName = new StringBuilder();

            for (int i = 0; i < last; i++) {
                userName.append(words[i].charAt(0));
            }

            userName.append(words[last]);
            customer.userName = userName.toString();

            // Tên: DUONG, Nguyen Van Hai
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

            // Ngày sinh: mm-dd-yyyy -> dd/mm/yyyy
            String[] date = customer.dayOfBirth.trim().split("-");
            customer.dayOfBirth =
                    date[1] + "/" + date[0] + "/" + date[2];

            // 4. Gửi lại: 8 byte requestId + đối tượng đã sửa
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write(requestId);

            try (ObjectOutputStream objectOutput =
                         new ObjectOutputStream(output)) {
                objectOutput.writeObject(customer);
                objectOutput.flush();
            }

            byte[] answer = output.toByteArray();

            socket.send(new DatagramPacket(
                    answer, answer.length, server, port
            ));

            System.out.println("Name: " + customer.name);
            System.out.println("Day of birth: " + customer.dayOfBirth);
            System.out.println("Username: " + customer.userName);
        } // 5. Đóng socket
    }
}