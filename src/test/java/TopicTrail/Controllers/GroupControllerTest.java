package TopicTrail.Controllers;

import TopicTrail.Repositories.GroupRepository;
import TopicTrail.Domain.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
class GroupRepositoryTest {

    @Autowired
    GroupRepository groupRepository;

    @BeforeEach
    public void setUp() {
        groupRepository.deleteAll().block();
    }

    @Test
    public void testSave() {
        Group group = new Group();
        group.setTitle("Test Group");

        Mono<Group> saveResult = groupRepository.save(group);

        StepVerifier.create(saveResult)
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    public void testFindByTitle() {
        Group group = new Group();
        group.setTitle("Test Group");

        Mono<Group> saveResult = groupRepository.save(group);

        Mono<Group> findByTitleResult = saveResult.flatMap(savedGroup ->
                groupRepository.findByTitle(savedGroup.getTitle())
        );

        StepVerifier.create(findByTitleResult)
                .assertNext(savedGroup -> assertEquals("Test Group", savedGroup.getTitle()))
                .expectComplete()
                .verify();
    }
}
