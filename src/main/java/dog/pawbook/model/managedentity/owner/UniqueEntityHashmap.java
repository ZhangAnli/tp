package dog.pawbook.model.managedentity.owner;

import static dog.pawbook.commons.util.CollectionUtil.requireAllNonNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import dog.pawbook.model.managedentity.Entity;
import dog.pawbook.model.managedentity.owner.exceptions.DuplicateOwnerException;
import dog.pawbook.model.managedentity.owner.exceptions.OwnerNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A hashmap of entities that enforces uniqueness between its elements and does not allow nulls.
 * A owner is considered unique by comparing using {@code Owner#isSameOwner(Owner)}. As such, adding and updating of
 * owners uses Owner#isSameOwner(Owner) for equality so as to ensure that the owner being added or updated is
 * unique in terms of identity in the UniqueOwnerList. However, the removal of a owner uses Owner#equals(Object) so
 * as to ensure that the owner with exactly the same fields will be removed.
 *
 * Supports a minimal set of list operations.
 *
 * @see Owner#isSameEntity(Entity)
 */

public class UniqueEntityHashmap<T extends Entity> implements Iterable<T> {

    private final ObservableList<T> internalList = FXCollections.observableArrayList();
    private final ObservableList<T> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    private HashMap<Integer, T> internalHashmap;
    private Integer nextId;

    /**
     * Constructs the hashmap for data storage.
     *
     * @param internalHashmap
     */
    public UniqueEntityHashmap(HashMap<Integer, T> internalHashmap) {
        this.internalHashmap = internalHashmap;
        nextId = 1;
    }

    /**
     * Returns true if the list contains an equivalent entity as the given argument.
     */
    public boolean contains(T toCheck) {
        requireNonNull(toCheck);
        return internalHashmap.values().stream().anyMatch(toCheck::isSameEntity);
    }

    /**
     * Adds an entity to the list.
     * The entity must not already exist in the list.
     */
    public void add(T toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateOwnerException();
        }
        internalHashmap.put(nextId, toAdd);
        internalList.add(toAdd);
        nextId++;
    }

    /**
     * Replaces the owner {@code target} in the list with {@code editedEntity}.
     * {@code target} must exist in the list.
     * The entity identity of {@code editedEntity} must not be the same as another existing entity in the list.
     */
    public void setEntity(int index, T editedEntity) {
        requireAllNonNull(index, editedEntity);

        if (index == -1) {
            throw new OwnerNotFoundException();
        }

        if (!internalHashmap.get(index).isSameEntity(editedEntity) && contains(editedEntity)) {
            throw new DuplicateOwnerException();
        }

        internalHashmap.replace(nextId, editedEntity);
        internalList.set(index, editedEntity);
    }

    /**
     * Removes the equivalent entity from the list.
     * The entity must exist in the list.
     */
    public void remove(int toRemove) {
        requireNonNull(toRemove);
        if (!internalHashmap.containsKey(toRemove)) {
            throw new OwnerNotFoundException();
        }
        internalHashmap.remove(toRemove);
        internalList.remove(toRemove);
    }

    /**
     * Replaces the internal hashmap with the new HashMap from the replacement.
     *
     * @param replacement UniqueEntityHashmap containing the new entities.
     */
    public void setEntities(UniqueEntityHashmap replacement) {
        requireNonNull(replacement);
        internalHashmap = replacement.getHashmap();
        List<T> temp = internalHashmap.values().stream().collect(toCollection(ArrayList::new));
        internalList.setAll(FXCollections.observableArrayList(temp));
    }

    /**
     * Replaces the contents of this list with {@code entities}.
     * {@code entities} must not contain duplicate entities.
     */
    public void setEntities(HashMap<Integer, T> newEntities) {
        requireAllNonNull(newEntities);
        if (!entitiesAreUnique(newEntities)) {
            throw new DuplicateOwnerException();
        }
        internalHashmap = newEntities;
        internalList.setAll(newEntities.values());
    }

    /**
     * Gets the hashmap storing entity mappings.
     *
     * @return hashmap storing entity mappings.
     */
    private HashMap<Integer, T> getHashmap() {
        return this.internalHashmap;
    }

    /**
     * Returns the backing list as an {@code ObservableList}.
     */
    public ObservableList<T> asObservableList() {
        return internalList;
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<T> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<T> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueEntityHashmap // instanceof handles nulls
                && internalHashmap.equals(((UniqueEntityHashmap) other).getHashmap()));
    }

    @Override
    public int hashCode() {
        return internalHashmap.hashCode();
    }

    /**
     * Returns true if {@code entities} contains only unique entities.
     *
     * @param entities
     */
    private boolean entitiesAreUnique(HashMap<Integer, T> entities) {
        for (int i = 0; i < entities.size() - 1; i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                if (entities.get(i).isSameEntity(entities.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
