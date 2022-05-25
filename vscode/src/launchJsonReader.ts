// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

import * as path from 'path';
import { parse } from 'jsonc-parser';
import { injectable } from 'inversify';
import { DebugConfiguration, Uri, WorkspaceFolder } from 'vscode';
import * as vscode from 'vscode';
import * as fs from 'fs-extra';

@injectable()
export class LaunchJsonReader {
    constructor(
    ) {}

    public async getConfigurationsForWorkspace(workspace: WorkspaceFolder): Promise<DebugConfiguration[]> {
        const filename = path.join(workspace.uri.fsPath, '.vscode', 'launch.json');

        if (!(await fs.pathExists(filename))) {
            return [];
        }

        const text = await fs.readFile(filename, "utf-8");
        const parsed = parse(text, [], { allowTrailingComma: true, disallowComments: false });
        if (!parsed.configurations || !Array.isArray(parsed.configurations)) {
            throw Error('Missing field in launch.json: configurations');
        }
        if (!parsed.version) {
            throw Error('Missing field in launch.json: version');
        }
        // We do not bother ensuring each item is a DebugConfiguration...
        return parsed.configurations;
    }

    public async getConfigurationsByUri(uri: Uri): Promise<DebugConfiguration[]> {
        const workspace = vscode.workspace.getWorkspaceFolder(uri);
        if (workspace) {
            return this.getConfigurationsForWorkspace(workspace);
        }
        return [];
    }
}
