package dog.pawbook.logic.commands;

import static dog.pawbook.logic.commands.CommandUtil.disconnectFromOwner;
import static dog.pawbook.model.managedentity.dog.Dog.ENTITY_WORD;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;

import dog.pawbook.logic.commands.exceptions.CommandException;
import dog.pawbook.model.Model;
import dog.pawbook.model.managedentity.Entity;
import dog.pawbook.model.managedentity.dog.Dog;
import dog.pawbook.model.managedentity.program.Program;
import javafx.util.Pair;

/**
 * Deletes a owner identified using it's displayed index from the database.
 */
public class DeleteDogCommand extends DeleteCommand<Dog> {

    public static final String MESSAGE_USAGE =
            COMMAND_WORD + ": Deletes the dog identified by ID.\n"
            + "Parameters: ID (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " " + ENTITY_WORD + " 1";

    public static final String MESSAGE_SUCCESS = String.format(MESSAGE_DELETE_SUCCESS_FORMAT, ENTITY_WORD);

    public DeleteDogCommand(Integer targetId) {
        super(targetId, Dog.class);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Dog dogToDelete = getEntityToDelete(model);
        int ownerId = dogToDelete.getOwnerId();

        // delete the ID of the dog from the owner first
        disconnectFromOwner(model, ownerId, targetId);

        // remove the dog ID from programs as well
        disconnectFromPrograms(model);

        // then actually delete the dog
        model.deleteEntity(targetId);

        return new CommandResult(MESSAGE_SUCCESS + dogToDelete);
    }

    private void disconnectFromPrograms(Model model) {
        List<Pair<Integer, Entity>> relatedPrograms = model.getUnfilteredEntityList().stream()
                .filter(idEntityPair -> idEntityPair.getValue() instanceof Program)
                .filter(idEntityPair -> ((Program) idEntityPair.getValue()).getDogIdSet().contains(targetId))
                .collect(toList());

        for (Pair<Integer, Entity> pair : relatedPrograms) {
            int programId = pair.getKey();
            Program program = (Program) pair.getValue();

            HashSet<Integer> newDogIdSet = new HashSet<>(program.getDogIdSet());
            newDogIdSet.remove(targetId);
            model.setEntity(programId, new Program(program.getName(), program.getSessions(),
                    program.getTags(), newDogIdSet));
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteDogCommand // instanceof handles nulls
                    && targetId.equals(((DeleteDogCommand) other).targetId)); // state check
    }

    @Override
    protected String getEntityWord() {
        return ENTITY_WORD;
    }
}
