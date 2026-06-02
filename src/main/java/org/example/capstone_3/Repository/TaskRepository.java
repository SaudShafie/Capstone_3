package org.example.capstone_3.Repository;

import org.example.capstone_3.Model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task,Integer> {
    Task findTaskById(Integer id);
}
