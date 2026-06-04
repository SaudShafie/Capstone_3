package org.example.capstone_3.Repository;

import org.example.capstone_3.Model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Integer> {
    Task findTaskById(Integer id);

    @Query("select t from Task t where t.learningGroup.id=?1")
    List<Task> findTasksByLearningGroupId(Integer learningGroupId);
}
