/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.release.deployment;

import lombok.RequiredArgsConstructor;

import org.springframework.data.release.utils.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Deployment functionality.
 *
 * @author Oliver Gierke
 * @author Mark Paluch
 */
@Component
@RequiredArgsConstructor
public class DeploymentOperations {

	private final ArtifactoryClient client;
	private final Logger logger;

	public void verifyAuthentication() {
		client.verify();
	}

	/**
	 * Promotes the artifacts identified by the given {@link DeploymentInformation}.
	 *
	 * @param information must not be {@literal null}.
	 */
	public void promote(DeploymentInformation information) {

		Assert.notNull(information, "DeploymentInformation must not be null!");

		if (information.getModule().getIteration().isPublic()) {
			logger.log(information.getModule(),
					"Skipping build promotion as it's a public version and was staged to OSS Sonatype.");
			return;
		}

		client.promote(information);
	}

	/**
	 * Rolls back the given {@link DeploymentInformation}.
	 *
	 * @param information must not be {@literal null}.
	 */
	public void rollback(DeploymentInformation information) {

		Assert.notNull(information, "DeploymentInformation must not be null!");

		if (information.getModule().getIteration().isPublic()) {
			logger.log(information.getModule(),
					"Skipping build rollback as it's a public version and was deployed to Maven Central directly,");
			return;
		}

		client.deleteArtifacts(information);
	}
}
