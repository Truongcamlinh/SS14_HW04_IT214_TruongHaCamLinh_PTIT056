from pathlib import Path
import sys

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import mm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import (
    BaseDocTemplate,
    Frame,
    PageTemplate,
    Paragraph,
    Spacer,
    Table,
    TableStyle,
    PageBreak,
)


def register_fonts():
    root = Path("/System/Library/Fonts/Supplemental")
    pdfmetrics.registerFont(TTFont("ArialVN", str(root / "Arial.ttf")))
    pdfmetrics.registerFont(TTFont("ArialVN-Bold", str(root / "Arial Bold.ttf")))


def footer(canvas, doc):
    canvas.saveState()
    canvas.setFont("ArialVN", 8)
    canvas.setFillColor(colors.HexColor("#64748b"))
    canvas.drawString(20 * mm, 12 * mm, "SS14 HW04 - Choreography và Orchestration")
    canvas.drawRightString(190 * mm, 12 * mm, f"Trang {doc.page}")
    canvas.restoreState()


def build_report(student_name, student_id, output_path):
    register_fonts()
    output = Path(output_path)
    output.parent.mkdir(parents=True, exist_ok=True)

    doc = BaseDocTemplate(
        str(output),
        pagesize=A4,
        leftMargin=20 * mm,
        rightMargin=20 * mm,
        topMargin=19 * mm,
        bottomMargin=20 * mm,
        title="SS14 HW04 - Sự đánh đổi giữa tự do và tập trung",
        author=student_name,
    )
    frame = Frame(doc.leftMargin, doc.bottomMargin, doc.width, doc.height, id="main")
    doc.addPageTemplates(PageTemplate(id="report", frames=frame, onPage=footer))

    styles = getSampleStyleSheet()
    title = ParagraphStyle(
        "TitleVN", parent=styles["Title"], fontName="ArialVN-Bold",
        fontSize=19, leading=24, alignment=TA_CENTER,
        textColor=colors.HexColor("#0f172a"), spaceAfter=10,
    )
    subtitle = ParagraphStyle(
        "Subtitle", parent=styles["Normal"], fontName="ArialVN",
        fontSize=10.5, leading=15, alignment=TA_CENTER,
        textColor=colors.HexColor("#475569"), spaceAfter=18,
    )
    h1 = ParagraphStyle(
        "H1VN", parent=styles["Heading1"], fontName="ArialVN-Bold",
        fontSize=14, leading=18, textColor=colors.HexColor("#0f4c81"),
        spaceBefore=8, spaceAfter=7,
    )
    h2 = ParagraphStyle(
        "H2VN", parent=styles["Heading2"], fontName="ArialVN-Bold",
        fontSize=11.5, leading=15, textColor=colors.HexColor("#155e75"),
        spaceBefore=7, spaceAfter=5,
    )
    body = ParagraphStyle(
        "BodyVN", parent=styles["BodyText"], fontName="ArialVN",
        fontSize=10, leading=15, textColor=colors.HexColor("#1e293b"),
        spaceAfter=6,
    )
    bullet = ParagraphStyle(
        "BulletVN", parent=body, leftIndent=12, firstLineIndent=-7,
        bulletIndent=3, spaceAfter=4,
    )
    box = ParagraphStyle(
        "BoxVN", parent=body, leftIndent=8, rightIndent=8,
        borderColor=colors.HexColor("#bae6fd"), borderWidth=0.8,
        borderPadding=8, backColor=colors.HexColor("#f0f9ff"),
        spaceBefore=6, spaceAfter=8,
    )

    story = [
        Paragraph("BÁO CÁO THIẾT KẾ GIẢI PHÁP", title),
        Paragraph("SS14 HW04 - Sự đánh đổi giữa tự do và tập trung", title),
        Paragraph(
            f"Sinh viên: <b>{student_name}</b> &nbsp;&nbsp;|&nbsp;&nbsp; "
            f"Lớp: <b>IT214</b> &nbsp;&nbsp;|&nbsp;&nbsp; Mã sinh viên: <b>{student_id}</b>",
            subtitle,
        ),
        Paragraph("1. Phân tích yêu cầu tích hợp Voucher", h1),
        Paragraph(
            "Voucher không chỉ là phép trừ tiền đơn giản. Luồng phải kiểm tra mã tồn tại, "
            "thời hạn, điều kiện giá trị đơn tối thiểu, phạm vi sản phẩm, số lượt còn lại và "
            "quyền sử dụng của khách hàng. Sau đó hệ thống giữ lượt dùng để tránh hai đơn cùng "
            "tiêu thụ một voucher, tính mức giảm và cập nhật số tiền Payment phải thu.", body),
        Paragraph("• Nhận input: customerId, voucherCode, tổng tiền ban đầu và thông tin đơn.", bullet),
        Paragraph("• Voucher Service xác thực và giữ lượt dùng theo orderId.", bullet),
        Paragraph("• Tính discount và finalAmount; Order cập nhật tổng tiền mới.", bullet),
        Paragraph("• Payment chỉ trừ finalAmount sau khi cập nhật thành công.", bullet),
        Paragraph("• Nếu bước sau lỗi: hoàn tiền nếu cần, giải phóng voucher và hủy đơn.", bullet),
        Paragraph("2. Giải pháp Choreography", h1),
        Paragraph(
            "Voucher Service lắng nghe OrderCreated, kiểm tra voucher rồi phát VoucherApplied. "
            "Order Service nhận event để cập nhật tổng tiền và phát event tiếp theo cho Payment. "
            "Mỗi service tự quyết định phản ứng, không có bộ điều phối trung tâm.", body),
        Paragraph(
            "OrderCreated → VoucherApplied → OrderAmountUpdated → PaymentSuccess → OrderCompleted",
            box,
        ),
        Paragraph("Ưu điểm", h2),
        Paragraph("• Các service độc lập, scale consumer riêng và ít phụ thuộc trực tiếp.", bullet),
        Paragraph("• Luồng bất đồng bộ chịu tải tốt và service tạm dừng có thể đọc bù event.", bullet),
        Paragraph("Hạn chế", h2),
        Paragraph("• Thêm Voucher làm tăng topic, event contract và quan hệ ngầm.", bullet),
        Paragraph("• Khó biết đơn đang dừng ở bước nào nếu thiếu tracing tập trung.", bullet),
        Paragraph("• Logic bù trừ bị phân tán, debug event trùng hoặc sai thứ tự khó hơn.", bullet),
        PageBreak(),
        Paragraph("3. Giải pháp Orchestration", h1),
        Paragraph(
            "Checkout Orchestrator quản lý thứ tự gọi Order, Voucher và Payment. Nó lưu ngữ cảnh "
            "của Saga để biết bước nào đã hoàn tất và gọi hành động bù trừ tương ứng khi có lỗi.", body),
        Paragraph(
            "Tạo Order PENDING → Giữ Voucher → Cập nhật finalAmount → Thanh toán → Order COMPLETED",
            box,
        ),
        Paragraph("Luồng bù trừ", h2),
        Paragraph("• Voucher sai: hủy Order.", bullet),
        Paragraph("• Payment lỗi: giải phóng Voucher, sau đó hủy Order.", bullet),
        Paragraph("• Xác nhận Order lỗi sau Payment: hoàn tiền, giải phóng Voucher, hủy Order.", bullet),
        Paragraph("4. Bảng so sánh", h1),
    ]

    table_data = [
        ["Tiêu chí", "Choreography", "Orchestration"],
        ["Thêm service", "Ban đầu dễ; về sau tăng event và quan hệ ngầm.",
         "Thêm bước rõ trong workflow; orchestrator lớn dần."],
        ["Giám sát", "Phải ghép log và trace từ nhiều topic.",
         "Dễ xem trạng thái và lỗi tại một nơi."],
        ["Độ trễ", "Bất đồng bộ, không chờ tuần tự.",
         "Thường cao hơn do nhiều lời gọi tuần tự."],
        ["Mở rộng", "Service và consumer scale độc lập.",
         "Phải scale orchestrator và tránh nút nghẽn."],
        ["Bảo trì", "Event graph phức tạp khi hệ thống lớn.",
         "Luồng dễ đọc nhưng phụ thuộc bộ điều phối."],
        ["Bù trừ", "Phân tán giữa các service.",
         "Chính sách compensate tập trung, dễ kiểm soát."],
    ]
    wrapped = [[Paragraph(str(cell), body) for cell in row] for row in table_data]
    table = Table(wrapped, colWidths=[31 * mm, 68 * mm, 68 * mm], repeatRows=1)
    table.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#0f4c81")),
        ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
        ("FONTNAME", (0, 0), (-1, 0), "ArialVN-Bold"),
        ("BACKGROUND", (0, 1), (-1, -1), colors.HexColor("#f8fafc")),
        ("GRID", (0, 0), (-1, -1), 0.5, colors.HexColor("#cbd5e1")),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("LEFTPADDING", (0, 0), (-1, -1), 6),
        ("RIGHTPADDING", (0, 0), (-1, -1), 6),
        ("TOPPADDING", (0, 0), (-1, -1), 6),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 6),
    ]))
    story.extend([
        table,
        Spacer(1, 8),
        Paragraph("5. Quyết định kiến trúc", h1),
        Paragraph(
            "Bài chọn Orchestration. Lý do chính là luồng hiện đã có Voucher, Loyalty Points và "
            "Notification; nếu tiếp tục thêm event tự do, chi phí quan sát và debug sẽ tăng nhanh. "
            "Orchestrator biến quy trình thành một workflow đọc được, có điểm quan sát chung và "
            "chính sách bù trừ rõ ràng.", body),
        Paragraph(
            "Đánh đổi được chấp nhận là orchestrator có thêm trách nhiệm và có thể trở thành nút "
            "nghẽn. Cách giảm rủi ro là giữ nghiệp vụ cốt lõi trong từng service, để orchestrator "
            "chỉ điều phối; đồng thời triển khai nhiều instance và lưu trạng thái Saga bền vững.", body),
        Paragraph("6. Thiết kế source demo", h1),
        Paragraph("• checkout-orchestrator (8080): điều phối và compensate.", bullet),
        Paragraph("• order-service (8081): tạo, cập nhật tiền, hoàn tất hoặc hủy đơn.", bullet),
        Paragraph("• voucher-service (8082): kiểm tra SAVE10, giữ và giải phóng voucher.", bullet),
        Paragraph("• payment-service (8083): thanh toán và hoàn tiền.", bullet),
        Paragraph(
            "Demo dùng WebClient và Mono để nối các bước. SagaContext ghi nhận Order, Voucher và "
            "Payment đã hoàn tất tới đâu. onErrorResume gọi compensate theo thứ tự ngược, nhờ đó "
            "luồng chính và luồng lỗi đều quan sát được tại Orchestrator.", body),
        Paragraph("7. Kết luận", h1),
        Paragraph(
            "Choreography phù hợp với ít bước, event ổn định và nhu cầu scale độc lập cao. Khi số "
            "service và nhánh bù trừ tăng, Orchestration dễ vận hành và bảo trì hơn. Với StoreX ở "
            "giai đoạn hiện tại, bộ điều phối tập trung là lựa chọn hợp lý cho checkout, miễn là "
            "không kéo logic nghiệp vụ từ các service vào orchestrator.", body),
        Paragraph("8. Kịch bản kiểm thử", h1),
        Paragraph(
            "Kịch bản thành công dùng voucher SAVE10 và số tiền 1.000.000 VND. Voucher giảm 10%, "
            "Payment thu 900.000 VND và Order kết thúc ở COMPLETED.", body),
        Paragraph(
            "Kịch bản voucher sai dùng mã SAI-MA. Orchestrator nhận lỗi từ Voucher Service và gọi "
            "Order Service chuyển đơn sang CANCELLED; Payment chưa được gọi.", body),
        Paragraph(
            "Kịch bản thanh toán lỗi dùng số tiền sau giảm vẫn lớn hơn 5.000.000 VND. Orchestrator "
            "giải phóng lượt giữ voucher và hủy Order. Nếu lỗi xảy ra sau khi Payment thành công, "
            "luồng bù trừ gọi refund trước khi giải phóng voucher.", body),
        Paragraph("9. Lưu ý khi triển khai production", h1),
        Paragraph("• Lưu SagaContext vào database thay vì chỉ giữ trong bộ nhớ tiến trình.", bullet),
        Paragraph("• Mọi lệnh pay, refund, apply và release phải có idempotency key theo orderId.", bullet),
        Paragraph("• Áp dụng timeout, retry có giới hạn và Circuit Breaker cho từng lời gọi.", bullet),
        Paragraph("• Gắn correlationId vào log và trace để theo dõi toàn bộ checkout.", bullet),
        Paragraph("• Có cơ chế retry hoặc hàng đợi riêng nếu một hành động compensate thất bại.", bullet),
    ])

    doc.build(story)


if __name__ == "__main__":
    if len(sys.argv) != 4:
        raise SystemExit("Usage: create_report.py <student_name> <student_id> <output_pdf>")
    build_report(sys.argv[1], sys.argv[2], sys.argv[3])
