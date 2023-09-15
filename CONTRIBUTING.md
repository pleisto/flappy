# Contribute to Flappy

Welcome and thank you interest in contributing to Flappy! Flappy is developed in the open and continually improved by the community. It is because of you that we can bring the best of AI to everyone.

This guide provides information on filing issues and guidelines for open source contributions. Please leave comments/suggestions if you find something is missing or incorrect.

## Did you find a bug?

- **Do not open up a GitHub issue if the bug is a security vulnerability**. Instead, please follow the instructions in [SECURITY.md](./SECURITY.md).
- **Ensure the bug was not already reported** by searching on GitHub under [Issues](https://github.com/pleisto/flappy/issues).
- If you're unable to find an open issue addressing the problem, [open a new one](https://github.com/pleisto/flappy/issues/new). Be sure to include a **title and clear description**, as much relevant information as possible, and a **code sample** or an **executable test case** demonstrating the expected behavior that is not occurring. Also, we **strongly recommend that you write your issue in English**. Issues written in other languages will be automatically translated into English by the bot, but this may cause misunderstandings between you and the maintainers.

## Set up your development environment

We use scripts written in TypeScript to manage the monorepo. So, you'll need to install [Node.js](https://nodejs.org/en/) no matter which language implementation you're working on.

```bash
# install Volta, a JavaScript toolchain manager
curl https://get.volta.sh | bash

# install Node.js
volta install node

# install dependencies
yarn install
```

Apart from JVM-based languages like Java, the implementations of Flappy in other languages are built on top of a core library written in Rust. So, you may also need to set up the Rust toolchain as follows:

```bash
# install Rust
curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh
```

## Preparation for commit

- Write tests for your code.
- Run all linting with auto fix. `yarn lint:fix`

## Committing

> This repo demonstrates git hooks integration with [cz-conventional-changelog](https://github.com/commitizen/cz-conventional-changelog).

### Git Message Scope

- If the change is related to `packages/*` then the scope is the name of the package,
- And, if the change is related to a functional module with a namespace (e.g. :accounts, :admin) then the scope is namespace,
- Else the scope name is empty.
- **Use `yarn commit` and not `git commit`**
- Write a [good commit message](http://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html)
- Push to your fork and [Submit a pull request](https://github.com/pleisto/q/compare/)
- Wait for us. We try to at least comment on pull requests within one business day.
- We may suggest changes.
- Please, squash your commits to a single one if you introduced a new changes or pushed more than
  one commit. Let's keep the history clean.

## Revert commits

If the commit reverts a previous commit, it should begin with `revert:`, followed by the header of the reverted commit.

The content of the commit message body should contain:

- information about the SHA of the commit being reverted in the following format: `This reverts commit <SHA>`,
- a clear description of the reason for reverting the commit message.
