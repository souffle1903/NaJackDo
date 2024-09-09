package com.najackdo.server.domain.notification.entity;

public enum NotificationType {
	
	BOOK_RENTAL_REQUEST, // 책 대여 요청
	BOOK_RENTAL_APPROVAL, // 책 가능 승인
	LIKE_BOOKCASE, // 책 좋아요

	// 책 반납 7일전, 3일전, 1일전
	BOOK_RETURN_REMINDER_7DAYS,
	BOOK_RETURN_REMINDER_3DAYS,
	BOOK_RETURN_REMINDER_1DAY,

}
