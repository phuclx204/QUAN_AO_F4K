package org.example.quan_ao_f4k.repository.general;

import org.example.quan_ao_f4k.model.general.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificationRepository extends JpaRepository<Notification, Integer>,
		JpaSpecificationExecutor<Notification> {
}
