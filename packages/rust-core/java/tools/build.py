#!/usr/bin/env python3

# Forked from https://github.com/apache/incubator-opendal/blob/main/bindings/java/tools/build.py

from argparse import ArgumentDefaultsHelpFormatter, ArgumentParser
from pathlib import Path
import shutil
import subprocess

TARGET_META = {
    "x86_64-unknown-linux-gnu": {
        "classifier": "linux-x86_64",
        "artifact_name": "libflappy_java_bindings.so",
    },
    "aarch64-unknown-linux-gnu": {
        "classifier": "linux-aarch_64",
        "artifact_name": "libflappy_java_bindings.so",
    },
    "x86_64-unknown-linux-musl": {
        "classifier": "linux_musl-x86_64",
        "artifact_name": "libflappy_java_bindings.so",
    },
    "aarch64-unknown-linux-musl": {
        "classifier": "linux_musl-aarch_64",
        "artifact_name": "libflappy_java_bindings.so",
    },
    "x86_64-pc-windows-gnu": {
        "classifier": "windows-x86_64",
        "artifact_name": "flappy_java_bindings.dll",
    },
    "aarch64-apple-darwin": {
        "classifier": "osx-aarch_64",
        "artifact_name": "libflappy_java_bindings.dylib",
    },
    "x86_64-apple-darwin": {
        "classifier": "osx-x86_64",
        "artifact_name": "libflappy_java_bindings.dylib",
    },
}

CLASSIFIER_META = {v["classifier"]: k for k, v in TARGET_META.items()}

if __name__ == "__main__":
    basedir = Path(__file__).parent.parent

    parser = ArgumentParser(formatter_class=ArgumentDefaultsHelpFormatter)
    parser.add_argument("--classifier", type=str)
    parser.add_argument("--target", type=str, default="")
    parser.add_argument("--profile", type=str, default="dev")
    args = parser.parse_args()

    cmd = ["cargo", "build", "--color=always", f"--profile={args.profile}"]

    if args.target:
        target = args.target
        if target not in TARGET_META:
            raise Exception(f"Unsupported target: {target}")
        classifier = TARGET_META[target]["classifier"]
    elif args.classifier:
        classifier = args.classifier
        if classifier not in CLASSIFIER_META:
            raise Exception(f"Unsupported classifier: {classifier}")
        target = CLASSIFIER_META[classifier]
    else:
        raise Exception("Missing target or classifier")

    print("#### target: " + target + ",classifier: " + classifier)

    command = ["rustup", "target", "add", target]
    print("$ " + subprocess.list2cmdline(command))
    subprocess.run(command, cwd=basedir, check=True)
    cmd += ["--target", target]

    output = basedir / "target" / "bindings"
    Path(output).mkdir(exist_ok=True, parents=True)
    cmd += ["--target-dir", output]

    print("$ " + subprocess.list2cmdline(cmd))
    subprocess.run(cmd, cwd=basedir, check=True)

    # History reason of cargo profiles.
    profile = "debug" if args.profile in ["dev", "test", "bench"] else args.profile
    artifact = TARGET_META[target]["artifact_name"]
    src = output / target / profile / artifact
    dst = basedir / "target" / "classes" / "native" / classifier / artifact
    dst.parent.mkdir(exist_ok=True, parents=True)
    shutil.copy2(src, dst)
