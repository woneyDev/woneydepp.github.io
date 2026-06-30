import io
from fastapi import APIRouter
from fastapi.responses import StreamingResponse
from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import mm
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, HRFlowable, Table, TableStyle

router = APIRouter()

# 한글 폰트가 없는 환경을 위해 영문 폴백 사용
# 운영 환경에서는 Dockerfile에서 나눔고딕 폰트를 추가하면 됩니다.
FONT = "Helvetica"
FONT_BOLD = "Helvetica-Bold"


def _build_pdf(data: dict) -> bytes:
    """포트폴리오 데이터를 받아 PDF 바이트 스트림을 반환합니다."""
    buffer = io.BytesIO()
    doc = SimpleDocTemplate(
        buffer,
        pagesize=A4,
        leftMargin=20 * mm,
        rightMargin=20 * mm,
        topMargin=20 * mm,
        bottomMargin=20 * mm,
    )

    styles = getSampleStyleSheet()
    title_style   = ParagraphStyle("Title",   fontName=FONT_BOLD, fontSize=20, spaceAfter=4)
    section_style = ParagraphStyle("Section", fontName=FONT_BOLD, fontSize=13, spaceAfter=4, textColor=colors.HexColor("#2563eb"))
    body_style    = ParagraphStyle("Body",    fontName=FONT,      fontSize=10, spaceAfter=3, leading=14)
    sub_style     = ParagraphStyle("Sub",     fontName=FONT_BOLD, fontSize=11, spaceAfter=2)

    story = []

    # ── Hero ──
    hero = data.get("hero", {})
    story.append(Paragraph(hero.get("title", "Portfolio"), title_style))
    story.append(Paragraph(hero.get("subtitle", ""), body_style))
    story.append(Paragraph(f"Email: {hero.get('email', '')}  |  GitHub: {hero.get('githubUrl', '')}", body_style))
    story.append(HRFlowable(width="100%", thickness=1, color=colors.HexColor("#e5e7eb")))
    story.append(Spacer(1, 4 * mm))

    # ── Skills ──
    story.append(Paragraph("Skills", section_style))
    skills = data.get("skills", [])
    if skills:
        rows = [["Skill", "Level"]] + [[s["name"], s["level"]] for s in skills]
        table = Table(rows, colWidths=[100 * mm, 60 * mm])
        table.setStyle(TableStyle([
            ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#2563eb")),
            ("TEXTCOLOR",  (0, 0), (-1, 0), colors.white),
            ("FONTNAME",   (0, 0), (-1, 0), FONT_BOLD),
            ("FONTSIZE",   (0, 0), (-1, -1), 10),
            ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, colors.HexColor("#f9fafb")]),
            ("GRID", (0, 0), (-1, -1), 0.5, colors.HexColor("#e5e7eb")),
            ("TOPPADDING",    (0, 0), (-1, -1), 3),
            ("BOTTOMPADDING", (0, 0), (-1, -1), 3),
        ]))
        story.append(table)
    story.append(Spacer(1, 5 * mm))

    # ── Projects ──
    story.append(Paragraph("Projects", section_style))
    for p in data.get("projects", []):
        story.append(Paragraph(f"{p['title']}  ({p['period']})", sub_style))
        story.append(Paragraph(p["description"], body_style))
        tech_str = " · ".join(p.get("techStack", []))
        if tech_str:
            story.append(Paragraph(f"Tech: {tech_str}", body_style))
        story.append(Spacer(1, 3 * mm))

    # ── Career ──
    story.append(Paragraph("Career", section_style))
    for c in data.get("career", []):
        story.append(Paragraph(f"{c['company']}  —  {c['role']}  ({c['period']})", sub_style))
        for ach in c.get("achievements", []):
            story.append(Paragraph(f"• {ach}", body_style))
        story.append(Spacer(1, 3 * mm))

    doc.build(story)
    buffer.seek(0)
    return buffer.read()


@router.post("/generate", summary="PDF 이력서 생성")
async def generate_pdf(data: dict):
    """
    포트폴리오 데이터(JSON)를 받아 PDF 파일을 반환합니다.

    Body 예시:
    {
        "hero": { "title": "...", "subtitle": "...", "email": "...", "githubUrl": "..." },
        "skills": [ { "name": "Java", "level": "Advanced" } ],
        "projects": [ { "title": "...", "description": "...", "period": "...", "techStack": [] } ],
        "career": [ { "company": "...", "role": "...", "period": "...", "achievements": [] } ]
    }
    """
    pdf_bytes = _build_pdf(data)
    return StreamingResponse(
        io.BytesIO(pdf_bytes),
        media_type="application/pdf",
        headers={"Content-Disposition": "attachment; filename=portfolio.pdf"},
    )
