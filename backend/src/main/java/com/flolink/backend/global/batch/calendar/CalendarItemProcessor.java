package com.flolink.backend.global.batch.calendar;

import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.flolink.backend.domain.calendar.entity.Calendar;
import com.flolink.backend.domain.fcm.event.FcmEvent;
import com.flolink.backend.domain.room.entity.UserRoom;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CalendarItemProcessor implements ItemProcessor<Calendar, Calendar> {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Override
	public Calendar process(Calendar calendar) {
		log.info("============== Calendar process START ================");
		List<UserRoom> userRooms = calendar.getRoom().getUserRoomList();
		for (UserRoom userRoom : userRooms) {
			System.out.println(userRoom.getUser().getFcm());
			if (userRoom.getUser().getFcm() != null) {
				try {
					FcmEvent fcmEvent = new FcmEvent(this, "오늘의 가족일정이 있어요.",
						calendar.getTitle(),
						userRoom.getUser().getFcm().getFcmToken());
					eventPublisher.publishEvent(fcmEvent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		log.info("============== Calendar process END ================");
		return calendar;
	}
}

