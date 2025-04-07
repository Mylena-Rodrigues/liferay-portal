/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* global Liferay */

import { useEffect, useState } from 'react';

import {
    convertMinutesToDuration,
    getCourseFirstLessonId,
    isSignedIn,
} from '../../utils/util';

import '../../index.scss';

import '../../styles/LearningPathSteps.scss';

import ClayIcon from '@clayui/icon';

import {
    enrollToLearningPath,
    getUserEnrollmentByLearningPathId,
    updateCompletedSteps,
} from '../../services/enrollment';
import { getLearningPath } from '../../services/learning-path';
import { getLearningPathSteps } from '../../services/learning-path-step';
import Banner from '../Common/TopBanner/TopBanner';

const LearningPathSteps = () => {
    const learningPathId = Liferay.ThemeDisplay.getLayoutURL().split('/').pop();

    const [learningPathSteps, setLearningPathSteps] = useState(null);
    const [learningPath, setLearningPath] = useState(null);
    const [isEnrolled, setIsEnrolled] = useState(false);
    const [userEnrollment, setUserEnrollment] = useState({
        id: 0,
        completedStepsIds: "",
    });



    useEffect(() => {
        const fetchLearningPathData = () => {
            getLearningPath(learningPathId)
                .then(
                    async (data) => {
                        setLearningPath({
                            description: data.description,
                            name: data.name,
                            persona: data.persona
                        });

                        if(data.learningPathSteps.length === 0) return;

                        Promise.all(
                            data.learningPathSteps.map(
                                async (step) => {
                                    const isCourse = step.type.key === 'course';
                                    const stepType = isCourse ? 'p2s3CourseToP2S3LPSteps' : 'p2S3ExtMediaToP2s3LPSteps';
                                    const stepResponse = await getLearningPathSteps(step.id, stepType);

                                    const stepTypeData = stepResponse[stepType];

                                    return {
                                        ...step,
                                        totalDuration: stepTypeData?.durationMinutes,
                                        url: isCourse ? await getCourseFirstLessonId(stepTypeData.id) : stepTypeData.id
                                    }
                                }
                            ).sort((step_a, step_b) => step_b?.position - step_a?.position)
                        ).then(
                            (stepsData) => {
                                setLearningPathSteps(stepsData);
                            }
                        )
                    }
                ).catch(error => 
                    console.error('Error fetching learning path:', error)
                )
        };

        fetchLearningPathData();
    }, [learningPathId]);

    useEffect(() => {
        const fetchUserEnrollment = () => {
            getUserEnrollmentByLearningPathId(learningPathId)
                .then(
                    (userEnrollment) => {
                        setUserEnrollment({
                            id: userEnrollment.id,
                            completedStepsIds: userEnrollment.completedStepsIds
                        });

                        setIsEnrolled(true);
                    }
                ).catch (error => console.error('Error fetching user enrollment:', error))
        }

        if (learningPathId) fetchUserEnrollment();
    }, [learningPathId])

    const getLearningPathTotalDuration = 
        learningPathSteps?.reduce(
            (finalTotal, step) => {
                const stepDuration = convertMinutesToDuration(step.totalDuration);

                return finalTotal + parseFloat(stepDuration);
        }, 0);

    const navigateToStepContent = (stepUrl) => {
        window.location.href = `/l/${stepUrl}`;
    };

    const isStepCompleted = async (stepId) => {
        return userEnrollment.completedStepsIds?.includes(stepId);
    }

    const registerAndCompleteStep = async (stepId, stepType) => {
        if (stepType === 'video') {
            if (isSignedIn() && !isEnrolled) {
                await enrollToLearningPath(learningPathId, stepId);
            } else {
                if (isEnrolled && !isStepCompleted(stepId)) {
                    await updateCompletedSteps(
                        userEnrollment,
                        stepId
                    );
                }
            }
        }
    }

    const handleNextStep = async (step) => {
        await registerAndCompleteStep(step.id, step.type.key);

        navigateToStepContent(step.url)
    }

    return (
        <>
            <Banner
                tag="learning-path"
                name={learningPath?.name}
                personas={learningPath?.persona}
                totalDuration={getLearningPathTotalDuration}
            />

            <div className='learning-path__details'>
                <p className='learning-path__details-description'>{learningPath?.description}</p>

                <div className='learning-path__details-steps'>
                    {learningPathSteps?.map((step, index) => {
                        return (
                            <div key={index} className='learning-path-step'>
                                <div className={`learning-path-step__number ${step.type.key}`}>
                                    <span>Step {index + 1}</span>
                                </div>

                                <div className='col col-11 px-0'>
                                    <div className='learning-path-step__info' onClick={() => handleNextStep(step)}>
                                        <h3 className='learning-path-step__info-title'>
                                            {step.title}
                                        </h3>

                                        <p className='learning-path-step__info-description'>
                                            {step.description}
                                        </p>

                                        <div className='learning-path-step__info-tags'>
                                            <div className='info-tag'>
                                                <p className='info-tag__content info-tag__content-duration'>
                                                    {convertMinutesToDuration(
                                                        step.totalDuration,
                                                        'hours'
                                                    )}
                                                </p>
                                            </div>

                                            {isStepCompleted(step.id) && (
                                                <div className='completed-tag ml-2'>
                                                    <p>Completed</p>
                                                    <ClayIcon
                                                        className="ml-2"
                                                        symbol="check"
                                                    />
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        )
                    }) || <p>No learning path available.</p>}
                </div>
            </div>
        </>
    );
};

export default LearningPathSteps;