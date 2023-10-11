# Flappy Monorepo

> :warning: **This project is still under development.** We're working hard to release the first version of Flappy as soon as possible. Stay tuned! Documentation and code examples will be available soon.

This monorepo gathers all the Flappy libraries, each implemented in a different programming language. Flappy is a library developed to streamline the creation of AI applications and agents that use Large Language Models (LLMs).

## Overview

Flappy is a production-ready Language Learning Model (LLM) Application/Agent SDK designed to simplify AI integration in your projects. It is an easy-to-use, universally compatible, and production-ready solution that brings the power of AI to developers regardless of their preferred programming language.

## Key Features

- **Ease of Use**: Flappy is designed to be as user-friendly as CRUD application development, minimizing the learning curve for developers new to AI.
- **Production-Ready**: Beyond research, Flappy is a robust SDK that balances cost-efficiency and sandbox security to provide a stable platform for commercial environments.
- **Language-Agnostic**: Flappy integrates seamlessly with any programming language, eliminating the need for Python unless explicitly required by your application.

## Core Components

### Agent Functions

In Flappy's ecosystem, an agent operates as a versatile conduit for the LLM, performing a variety of tasks such as structuring data, invoking external APIs, or sandboxing LLM-generated Python code as needed. This design philosophy caters to the increasing demand for LLM-based AI applications across various sectors.

The agent functions in Flappy are built on two foundational types:

1. **InvokeFunction**: This function allows agents to interact with the environment. It's defined by input and output parameters, facilitating efficient interaction with the LLM.
2. **SynthesizedFunction**: This function is processed by the LLM and only requires the definition of its description and the structure of its inputs and outputs.

#### Function Implementation Details

Flappy introduces unique implementation mechanisms to enhance these functions:

- **Unique JSON Schema Integration**: Users can define abstract classes in their preferred programming language, which Flappy then transforms into JSON Schema schemas. This feature enhances machine readability and interoperability and manages the inputs and outputs of LLMs, providing controlled, consistent, and machine-readable data.
- **AST Parsing**: Flappy performs Abstract Syntax Tree (AST) parsing on the LLM outputs to ensure that the generated JSON data strictly adheres to the defined JSON Schema schema.

### Code Interpreter

Flappy's Code Interpreter serves as a secure environment to execute Python or TypeScript code produced by LLMs. It provides a sandboxed safety feature that reduces runtime errors and potential security vulnerabilities, making it suitable for deployment in a production environment.

### LLM Abstraction Layer

To enhance the flexibility of application development, Flappy provides an abstraction layer for different LLMs. This feature allows users to easily switch between different LLMs and designate fallback LLMs, ensuring application stability.

Flappy empowers developers to build LLM-based applications in a language-agnostic way. Start your journey with Flappy today and harness the immense potential of AI in your preferred programming language.

## Getting Started

To begin with Flappy, select your preferred language implementation from the list below:

- [Node.js](./packages/nodejs/README.md)
- [Kotlin&Java](./packages/kotlin/README.md)
- Ruby (coming soon)
- PHP (coming soon)
- [C#](./packages/csharp/README.md)
- Go (coming soon)
- Python (coming soon)

## Contributing

We're thrilled to receive contributions from the community! For more details, please refer to our [contributing guidelines](./CONTRIBUTING.md).

## License

This project is covered under the [Apache License 2.0](./LICENSE). Copyright (c) 2023 Pleisto Inc.
