## Setup

To run this application just import this project on Android Studio v3.3+

Build the project & run it on any Android device with min SDK level 24


## TO-DO

- [x] LoginActivity
- [x] RegistrationActivity
- [x] ViewContactsActivity
- [x] ViewMessagesActivity
- [x] Client side cache support
- [x] Search bar in ViewContacts
- [ ] Verify all background threads using strictMode class
- [x] index relevant columns in cache
- [ ] Add support for Phone Numbers
- [x] Sign in with Google & Fb (OAuth)
- [ ] Implement an Activity to add new Contact
- [ ] integrate loginActivity with GCP server
- [x] integrate RegistrationActivity with GCP server
- [ ] integrate ViewContacts with GCP server
- [ ] integrate ViewMessages with GCP server
- [ ] delete cache content if it grows too large


## Bugs
- Search in ViewContacts not working when user hits delete button

## Possible Enhancements
- Find out better ways to synchronize various DB & Network threads rather than just using conditionVariables.
	- When a user is typing a username to send a message for the first time, then show suggestions while typing (Probably with the current set of API's it's not possible to do so).
