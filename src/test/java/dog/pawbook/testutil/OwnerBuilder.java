package dog.pawbook.testutil;

import java.util.HashSet;
import java.util.Set;

import dog.pawbook.model.managedentity.owner.Address;
import dog.pawbook.model.managedentity.owner.Email;
import dog.pawbook.model.managedentity.owner.Owner;
import dog.pawbook.model.managedentity.owner.Phone;
import dog.pawbook.model.util.SampleDataUtil;

/**
 * A utility class to help with building Owner objects.
 */
public class OwnerBuilder extends EntityBuilder<OwnerBuilder, Owner> {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";

    private Phone phone;
    private Email email;
    private Address address;
    private Set<Integer> dogIdSet;

    /**
     * Creates a {@code OwnerBuilder} with the default details.
     */
    public OwnerBuilder() {
        super(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        dogIdSet = new HashSet<>();
    }

    /**
     * Initializes the OwnerBuilder with the data of {@code ownerToCopy}.
     */
    public OwnerBuilder(Owner ownerToCopy) {
        super(ownerToCopy);
        phone = ownerToCopy.getPhone();
        email = ownerToCopy.getEmail();
        address = ownerToCopy.getAddress();
        dogIdSet = new HashSet<>(ownerToCopy.getDogIdSet());
    }

    /**
     * Sets the {@code Address} of the {@code Owner} that we are building.
     */
    public OwnerBuilder withAddress(String address) {
        this.address = new Address(address);
        return self();
    }

    /**
     * Sets the {@code Phone} of the {@code Owner} that we are building.
     */
    public OwnerBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return self();
    }

    /**
     * Sets the {@code Email} of the {@code Owner} that we are building.
     */
    public OwnerBuilder withEmail(String email) {
        this.email = new Email(email);
        return self();
    }

    /**
     * Parses the {@code tags} into a {@code Set<Integer>} and set it to the {@code Owner} that we are building.
     */
    public OwnerBuilder withDogs(Integer... dogIds) {
        this.dogIdSet = SampleDataUtil.getIdSet(dogIds);
        return self();
    }

    @Override
    protected OwnerBuilder self() {
        return this;
    }

    @Override
    public Owner build() {
        return new Owner(name, phone, email, address, tags, dogIdSet);
    }

}
