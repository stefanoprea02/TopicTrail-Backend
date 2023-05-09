package TopicTrail.Services;

import TopicTrail.Domain.Group;
import TopicTrail.Repositories.GroupRepository;
import TopicTrail.Repositories.PostRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;

    public GroupServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public Mono<Group> save(Group group) {
        return groupRepository.save(group);
    }

    @Override
    public Mono<Void> delete(String Id) {
        return groupRepository.deleteById(Id);
    }
}
