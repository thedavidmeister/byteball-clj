# byteball-clj

This is a network/validation engine for byteball units.

Currently WIP but goal is to provide an alternative implementation of the byteball protocol - http://byteball.org/

This implementation should be different enough to `byteballcore` (https://github.com/byteball/byteballcore) to stimulate discussion and progress, such as:

- Could validation be handled in parallel?
- Could core functionality (crypto, networking, validation, etc.) be split cleanly from higher level wallet features (UI, chatbots, etc.)?
- Could different storage backends/models be provided and beneficial?

Additionally, there are essentially no tests in `byteballcore` which presents a huge risk to the network. While building out this alternative implementation, new tests will be written against both repos simultaneously to help ensure compatibility and reliability.

## Current status

Working on logging in to a hub.

- [x] port serialisation logic for data
- [x] websockets connect to a hub and push data
- [x] crypto for signing a challenge message
- [ ] tests and cleanup
- [ ] send challenge message back to hub and receive login confirmation
