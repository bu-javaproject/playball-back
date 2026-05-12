package com.playball.backend.compliment.enums;

/**
 * 칭찬 태그 5종. DB의 compliment_tag.tag 컬럼 + member_compliment_summary.tag 와 매핑.
 */
public enum ComplimentTag {
    MANNERS,        // 👍 매너 좋아요
    SKILL,          // 💪 실력 좋아요
    PUNCTUAL,       // ⏰ 시간 약속 잘 지켜요
    PASSIONATE,     // 🔥 열정적이에요
    MOOD_MAKER      // 😄 분위기 메이커
}
